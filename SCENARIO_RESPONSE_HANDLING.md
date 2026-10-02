# Scenario response handling: analysis and plan

Status: analysis complete, steps A and A2 implemented (sample ITs pending), steps B–D open.

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

### B. True synchronous request handling (minor release)

Bypass queue and futures for the initiating request in sync mode:

- Pass the request into the execution's `TestContext` (new default overload, e.g.
  `ScenarioExecutorService.run(scenario, name, params, Consumer<TestContext> contextInitializer)`).
- `ScenarioEndpoint.receive(context)` takes the request from the context; `send(response, context)` stores the
  response in the context; the adapter reads it after `run()` returns.
- Removes the cross-thread hand-off entirely for sync mode and eliminates any cross-talk on singleton scenarios.
- Fail fast with a clear message when `correlation()` is used in sync mode (it cannot work there).

### C. Async mode hardening (minor release)

- Bounded executor queue with rejection → 503, instead of silent queueing until timeout.
- Stop blocking on missing responses in async mode: when an execution ends without responding, complete the future
  bound to *its* `TestContext` with "no response". Also covers actions failing before the request was received.
- Fix the double `registerException` / FIFO `fail(Throwable)` for generic exceptions (finding 3) by failing the
  context-bound future instead of the oldest one.
- Optional: for HTTP, return the future to Spring MVC (servlet async, `CompletableFuture<ResponseEntity<?>>`) instead
  of the blocking Citrus `HttpMessageController`, freeing Tomcat threads while waiting. SOAP and JMS stay blocking.

### D. Sync only (major release, not recommended)

Removing futures, async mode and correlation entirely would drop public API (`correlation()`, `CorrelationHandler*`,
`citrus.simulator.mode`) and break the intermediate-message pattern, the `GoodNightScenario`/`FaxCancelledScenario`
samples, the jms/mail/rest/ws archetypes and the `intermediate-messages.adoc` chapter. A + B deliver the performance
benefit without that cost.
