# Scenario response handling: analysis and plan

Status: analysis complete, steps A, A2, B and C implemented (C without the optional servlet-async part), step D not planned.

## Problem

`SimulatorEndpointAdapter` hands every inbound request to a scenario through a `CompletableFuture` and then blocks the
calling thread (for HTTP/SOAP: a Tomcat worker) in `responseFuture.get(citrus.simulator.default-timeout)` until the
scenario's `send()` completes the future. If no response is ever produced the thread is blocked for the full timeout
(5 s by default). Simulators with a high synchronous request volume run out of worker threads.

## How the hand-off works today

```
request thread                                        scenario (same thread in sync mode,
                                                      executor thread in async mode)
──────────────                                        ─────────────────────────────────
SimulatorEndpointAdapter.dispatchMessage
  ScenarioEndpoint.add(request, future) ─────────────► ScenarioEndpoint.receive(context)
  ScenarioExecutorService.run(...)                       pendingFutures → activeFutures[context]
  future.get(defaultTimeout)  ◄─────────────────────── ScenarioEndpoint.send(response, context)
                                                         activeFutures[context].complete(response)
```

`ScenarioEndpoint` keeps three collections (`pendingFutures` keyed by message, `activeFutures` keyed by `TestContext`,
`orderedFutures` FIFO) because OpenAPI/WSDL generated scenarios (`HttpScenarioGenerator`, `WsdlScenarioGenerator`) are
registered as **singletons** — one `ScenarioEndpoint` is shared by all concurrent requests to that operation.
`@Scenario`/`@Starter` beans are prototypes and get a fresh endpoint per request.

## Why the futures exist

The future is a cross-thread hand-off. It is genuinely required in exactly two cases:

1. **Async mode** (`citrus.simulator.mode=async`): the scenario runs on the executor thread pool, so the request thread
   has to wait for something.
2. **Intermediate messages** (`correlation()`): a follow-up request arrives on a *different* request thread, is routed
   by `CorrelationHandlerRegistry` into an already running scenario, and can only receive its response via a future.

Case 2 requires a scenario that keeps running *after* it answered the first request. That only works in async mode:
in sync mode the first response is not returned before the whole scenario has finished, so the client can never send
the follow-up. Accordingly every sample using `correlation()` (`GoodNightScenario` in sample-rest/-ws/-jms/-mail/
-combined, `FaxCancelledScenario` in sample-jms-fax) sets `citrus.simulator.mode=async`.

**Conclusion: in sync mode the future wait is pure overhead.** When `run()` returns, the scenario has completed —
either the response is already there or it never will be.

## Findings

1. **Sync mode waits for nothing.** `dispatchMessage` always calls `future.get(defaultTimeout)`, even when the scenario
   already completed on the same thread without sending a response. Visible in
   `SimulatorEndpointAdapterIT.dispatchMessage_returnsNull_withoutResponse`, which takes the full timeout in both modes.
2. **Failing test actions (`fail(...)`, failed validation, …) never complete the future.**
   `DefaultTestCase.executeAction` wraps every action failure in `TestCaseFailedException`;
   `DefaultScenarioExecutorService` only logged it. The request thread therefore waited `default-timeout` and the
   client then received `defaultStatusCode` (200) with an empty body — a failed simulation looking like a success.
   The #271 fix had documented that as intended ("fail on purpose = successful simulation"); this has been revised:
   a failing simulation answers 555, regardless of whether an exception or a failing action caused it.
3. **`ScenarioEndpoint.fail(Throwable)` completes the oldest future (FIFO).** With a shared singleton endpoint and
   concurrent requests this can fail *another* request's future. In async mode a generic exception additionally calls
   `registerException` twice (once in `createAndRunScenarioRunner`, once via `exceptionally(...)`).
4. **Async executor queue is unbounded** (`newFixedThreadPool`). Under saturation requests time out on the request
   thread while their scenario still runs later against an already cancelled future.

## Plan

### A. Stop blocking in sync mode (patch, no breaking API change) — **implemented**

- `ScenarioExecutorService#isSynchronous()` (new default method, `false`): tells callers whether `run()` only returns
  after the scenario completed. `DefaultScenarioExecutorService` returns `true`, `AsyncScenarioExecutorService`
  `false`. Custom implementations keep the current (waiting) behaviour; subclasses of `DefaultScenarioExecutorService`
  that move execution to another thread must override it.
- `SimulatorEndpointAdapter.dispatchMessage`: if the executor is synchronous and the future is not done after `run()`
  returned, cancel it and return `null` immediately instead of waiting `default-timeout`.

Observable behaviour (sync mode only): scenarios that complete without sending a response answer immediately
instead of after `default-timeout`.

### A2. Failing test actions answer 555 (both modes) — **implemented**

- `ScenarioEndpoint#fail(TestContext, Throwable)` (new): fails only the future of the request received in the given
  execution's `TestContext`. No FIFO fallback, so concurrent requests on singleton scenarios are never affected; a
  no-op if the response has already been sent.
- `DefaultScenarioExecutorService` calls it on `TestCaseFailedException`, so a failed action answers 555
  immediately — in sync *and* async mode (async mode no longer waits `default-timeout` in this case either).
- **Custom fallback endpoint adapters keep precedence for failed test actions.** `ws-support.adoc` and
  `rest-support.adoc` document the configurer's `fallbackEndpointAdapter()` as the handler for "unmatched requests or
  validation errors"; Citrus' `AbstractEndpointAdapter` delegates to it whenever `dispatchMessage` returns no response,
  which is what a failed test action used to produce. Therefore, if a failed test action (recognised by its
  `TestCaseFailedException`, which the executor passes on unwrapped) meets a fallback that is not the default
  `EmptyResponseEndpointAdapter`, `SimulatorEndpointAdapter` returns no response and Citrus hands the request to the
  fallback (e.g. the WSDL sample's `HELLO:ERROR-1001` SOAP fault). Everyone else gets 555. Exceptions thrown by the
  scenario code always answer 555, as established by #271.
- `FailScenario`/`ThrowScenario` Javadoc, `SimulatorRestIT.testSimulationFailingExpectantly` and
  `simulation-errors-handling.adoc` updated accordingly.

Behaviour change for the release notes (minor release): **a scenario whose test action fails before it has
responded now answers with HTTP 555 instead of an empty `defaultStatusCode` (200) response after `default-timeout`,
unless a custom fallback endpoint adapter is configured, which then handles the request (immediately) as before.**

Known limitation: an action failing *before* the scenario received its request has no context-bound future to fail.
Sync mode then answers "no response" immediately (A), async mode still waits `default-timeout` (see C).

Alternatives considered: always 555 (would break the documented fallback contract for users with a custom fallback),
and reverting A2 (failed simulations would keep looking successful for everyone else).

### B. True synchronous request handling (minor release) — **implemented**

In sync mode the initiating request no longer goes through the message channel, futures or any waiting:

- `ScenarioExecutorService#run(scenario, name, params, Consumer<TestContext>)` (new default method, throws
  `UnsupportedOperationException`): lets the caller initialize the execution's `TestContext` before the scenario runs.
  `DefaultScenarioExecutorService` implements it, `AsyncScenarioExecutorService` rejects it. Executors returning
  `isSynchronous() == true` must implement it. The initializer reaches `createTestContext()` via a consume-once
  thread-local, so the protected `startScenario(...)` hook keeps its signature for existing subclasses.
- `ScenarioEndpoint#bind(TestContext, Message)` / `#unbind(TestContext)` (new): the request is bound to the
  execution's context. The first `receive` in that context consumes it, the first `send` answers it, `fail(context, e)`
  fails it; `unbind` returns the response (or a `SimulationFailedUnexpectedlyException`, or `null`). Matching is by
  context identity, so concurrent executions of singleton (OpenAPI/WSDL) scenarios cannot see each other's messages.
- `SimulatorEndpointAdapter`: if the executor is synchronous it binds the request, runs the scenario and returns the
  unbound response. The step-A "is the future done?" check is gone, as sync mode no longer creates a future.
- `SimulatorScenario#registerException` fails the request of the execution's own context first and only falls back to
  the oldest queued request (FIFO) if the failure cannot be attributed. This also fixes the cross-talk part of
  finding 3 for exceptions thrown after the request has been received.
- Unchanged: intermediate messages (`correlation()`) still use the channel and futures, and take precedence over the
  bound request in `send`. Async mode is unchanged.

Behaviour notes:

- A scenario must send its response within its own execution `TestContext`, as every DSL action does. Code that
  calls `getScenarioEndpoint().send(message, someOtherContext)` with a self-made context no longer answers the request
  in sync mode (`SimulatorEndpointAdapterIT.SuccessScenario` did that and was adjusted).
- The proposed "fail fast when `correlation()` is used in sync mode" was not implemented: correlation keeps behaving as
  before in sync mode, and failing it would be a breaking change of its own.

Verification (A, A2 and B together): `./mvnw -pl simulator-spring-boot install` and
`./mvnw -f simulator-samples/pom.xml verify` green.

### C. Async mode hardening (minor release) — **implemented**

Async mode now binds the initiating request to its execution as well, exactly like sync mode (B); the message channel
and its futures remain in use for intermediate messages (`correlation()`) only.

- `DefaultScenarioExecutorService#supportsTestContextInitialization()` returns `true`, also for
  `AsyncScenarioExecutorService`: the test context initializer is taken from the calling thread and handed over to the
  executor thread (`takeTestContextInitializer()` / `runWithTestContextInitializer(...)`, protected for subclasses).
  `ScenarioExecutorService#supportsTestContextInitialization()` (new default method, `false`) tells callers whether
  they can rely on that; custom executors keep the previous channel-based hand-off.
- `ScenarioEndpoint#bind(context, request, responseFuture)` / `#release(context)` replace B's `bind`/`unbind` (B is
  unreleased). The executor releases the requests of an execution once it has ended — in a `finally`, after
  `runner.stop()` — so unanswered requests (bound or intermediate) are completed with "no response" right away.
  Exceptions escaping the execution fail its unanswered requests first (555).
- `SimulatorEndpointAdapter` uses one code path for both modes: bind, run, await the future. In sync mode the future is
  already completed when the executor returns; in async mode it is completed as soon as the scenario responds, fails or
  ends. Only executions still running after `default-timeout` are answered without response, as before.
- **Finding 3 fixed:** `AsyncScenarioExecutorService` no longer calls `registerException` a second time from
  `exceptionally(...)`, which could fail the oldest queued request of *another* execution. Failures are registered
  within the execution, attributed to its own requests; failures escaping it are logged.
- **Finding 4 fixed (opt-in):** `citrus.simulator.executor-queue-capacity` (new, default unbounded = previous
  behaviour) bounds the executor queue. Rejected executions are completed as failed in the database and answered with
  HTTP 503 (Service Unavailable) right away, instead of queueing up until the caller times out.
- Not done (optional): returning the future to Spring MVC (servlet async) for HTTP, to free Tomcat threads while
  waiting in async mode. With immediate completion on response/failure/end, the remaining wait is the scenario's own
  run time, so the benefit is smaller now; worth it only for long-running async scenarios under high load.

Non-breaking: the new property defaults to the previous behaviour, new interface methods have defaults, custom executors
keep working through the channel path. Observable changes (async mode): requests whose scenario ends without
responding are answered immediately instead of after `default-timeout`.

Also fixed in passing: `concepts-advanced.adoc` documented a non-existent `citrus.simulator.executor.threads` property
(actual: `executor-threads`).

Verification: `./mvnw -pl simulator-spring-boot install` (388 unit, 336 integration tests) and
`./mvnw -f simulator-samples/pom.xml verify` (all 11 samples) green. `AsynchronousSimulatorEndpointAdapterIT`
"no response": ~6 s → ~1.2 s.

### D. Sync only (major release, not recommended)

Removing futures, async mode and correlation entirely would drop public API (`correlation()`, `CorrelationHandler*`,
`citrus.simulator.mode`) and break the intermediate-message pattern, the `GoodNightScenario`/`FaxCancelledScenario`
samples, the jms/mail/rest/ws archetypes and the `intermediate-messages.adoc` chapter. A + B deliver the performance
benefit without that cost.
