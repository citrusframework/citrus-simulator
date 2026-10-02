# Scenario response handling: analysis and plan

Status: analysis complete, step A implemented (sample ITs pending), steps B–D open.

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
2. **Forced failures (`fail(...)` action, failing validation) block until the timeout.** `DefaultTestCase.executeAction`
   wraps every action failure in `TestCaseFailedException`; `DefaultScenarioExecutorService` deliberately only logs it.
   By design (`fix(#271)`, asserted by `SimulatorRestIT.testSimulationFailingExpectantly`) such a scenario answers with
   *no response*, i.e. Citrus' `HttpMessageController` returns `defaultStatusCode` (200) with an empty body, while only
   exceptions thrown from the scenario code itself answer with 555. The semantics are fine — but the "no response" is
   only produced after the request thread waited `default-timeout`, because nothing ever completes the future.
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

Observable behaviour (sync mode only): scenarios that complete without a response — including forced failures —
answer immediately with the same result as before (`null` → `defaultStatusCode`), instead of after `default-timeout`.
Responses, 555 for exceptions, correlation, async mode, JMS `handleResponse=false` and starters are unchanged.

Verification: `./mvnw -pl simulator-spring-boot verify` green; in `SynchronousSimulatorEndpointAdapterIT` "no response"
dropped from ~6 s to ~0.04 s and a forced failure answers in ~0.1 s. The sample ITs (`simulator-samples`) still need to
be run — they bind to port 8080.

> An earlier draft of this plan also completed the future with a 555 on forced failures. That contradicts the
> intended #271 semantics above and was dropped.

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
- Stop blocking on forced failures / missing responses in async mode (finding 2): when an execution ends, complete the
  future bound to *its* `TestContext` (`activeFutures`) with "no response" if it is still open. Context-bound, so
  concurrent requests on singleton scenarios are unaffected.
- Fix the double `registerException` / FIFO `fail(Throwable)` for generic exceptions (finding 3) by failing the
  context-bound future instead of the oldest one.
- Optional: for HTTP, return the future to Spring MVC (servlet async, `CompletableFuture<ResponseEntity<?>>`) instead
  of the blocking Citrus `HttpMessageController`, freeing Tomcat threads while waiting. SOAP and JMS stay blocking.

### D. Sync only (major release, not recommended)

Removing futures, async mode and correlation entirely would drop public API (`correlation()`, `CorrelationHandler*`,
`citrus.simulator.mode`) and break the intermediate-message pattern, the `GoodNightScenario`/`FaxCancelledScenario`
samples, the jms/mail/rest/ws archetypes and the `intermediate-messages.adoc` chapter. A + B deliver the performance
benefit without that cost.
