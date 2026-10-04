---
paths:
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/endpoint/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/scenario/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/correlation/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/listener/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/service/runner/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/service/ScenarioExecutorService.java"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/http/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/ws/**"
  - "simulator-spring-boot/src/*/java/org/citrusframework/simulator/jms/**"
  - "simulator-samples/**"
  - "simulator-docs/design/scenario-response-handling.md"
---

# Scenario execution and response handling

How an inbound request reaches a scenario and how its response gets back. This is the most concurrency-sensitive
part of the code base; read this before changing anything in it. Design history and rejected alternatives are in
`simulator-docs/design/scenario-response-handling.md`.

## Request lifecycle

1. A transport (REST `HttpMessageController`, SOAP `MessageDispatcherServlet`, JMS/channel `SimulatorEndpointPoller`)
   calls Citrus' `AbstractEndpointAdapter.handleMessage(request)` on a `SimulatorEndpointAdapter`. Each transport
   auto-configuration creates its own adapter instance.
2. `handleMessageInternal`: if a `CorrelationHandler` matches, the request is an *intermediate message* for an already
   running scenario → `handleMessageWithCorrelation` (channel path, see below). Otherwise `dispatchMessage`: the
   `ScenarioMapper` key selects the scenario bean (fallback: `citrus.simulator.default-scenario`).
3. `dispatchMessage` picks the hand-off:
   - `ScenarioExecutorService#supportsTestContextInitialization()` → `dispatchMessageToExecution` (both built-in
     executors): a `CompletableFuture` is created, the executor's test context initializer calls
     `ScenarioEndpoint#bind(context, request, future)`, then the adapter awaits the future.
   - otherwise (custom executors) → `dispatchMessageThroughChannel`: legacy `ScenarioEndpoint#add` + queue.
4. Inside the execution (`DefaultScenarioExecutorService#createAndRunScenarioRunner`) the scenario's first `receive`
   in its `TestContext` takes the bound request, its first `send` completes the future. In a `finally`, **after**
   `runner.stop()`, the executor calls `ScenarioEndpoint#release(context)`, completing every unanswered request of that
   execution with `null` ("no response").

Consequences: in sync mode the future is always completed when `run()` returns, so nothing ever waits. In async mode
the adapter waits only as long as the scenario actually runs; `citrus.simulator.default-timeout` only matters for
executions still running (or still queued) when it expires.

## Response semantics

| Outcome of the execution                                  | Answer                                                        |
|-----------------------------------------------------------|---------------------------------------------------------------|
| scenario sent a response                                  | that response                                                 |
| ended without response, or still running after timeout   | `null` → Citrus delegates to the fallback endpoint adapter     |
| a test action failed (`TestCaseFailedException`)          | custom fallback adapter if configured, otherwise HTTP **555** |
| scenario code threw an exception                          | HTTP **555** (always, see #271)                               |
| async executor queue full (`executor-queue-capacity`)     | HTTP **503**, execution persisted as failed                   |
| JMS adapter with `handleResponse=false`                   | `null` (one-way)                                              |

- "Custom fallback" = `getFallbackEndpointAdapter()` is non-null and not `EmptyResponseEndpointAdapter` (the default
  of every `Simulator*Adapter` configurer). Returning `null` from `dispatchMessage` is how a request reaches it:
  `AbstractEndpointAdapter.handleMessage` delegates when the reply is `null`. See `handleSimulationFailure`.
- Failures travel as a `SimulationFailedUnexpectedlyException` message (type `EXCEPTION_TYPE`) whose payload is the
  cause. The adapter tells failed actions from thrown exceptions **only** by `cause instanceof TestCaseFailedException`,
  so `DefaultScenarioExecutorService` must pass that exception on unwrapped.

## Invariants — do not break

- **Attribute by `TestContext` identity, never by order.** `ScenarioEndpoint` keys everything by context identity
  (`IdentityHashMap`). OpenAPI/WSDL generated scenarios (`HttpScenarioGenerator`, `WsdlScenarioGenerator`) are
  **singletons**: one `ScenarioEndpoint` is shared by all concurrent requests to that operation. `@Scenario`/`@Starter`
  beans are prototypes. Anything "first/oldest/next" across contexts answers the wrong client.
- **Do not use the FIFO paths in new code.** `ScenarioEndpoint#fail(Throwable)` / `orderedFutures` complete the oldest
  queued future of *any* execution. They only remain as a last-resort fallback in
  `SimulatorScenario#registerException` and for custom executors. Use `fail(TestContext, Throwable)`.
- **Responses must be sent within the execution's own `TestContext`.** Every DSL action does that. Calling
  `getScenarioEndpoint().send(message, someOtherContext)` with a self-made context does not answer the request.
- **Every execution must release its context**, also on failure. If you restructure `createAndRunScenarioRunner`, keep
  the order: scenario → `fail(context, e)` on failure → `runner.stop()` → `release(context)`.
- **Never block the request thread without a reason.** In sync mode the scenario has completed when `run()` returns.
- **The protected `startScenario(Long, String, SimulatorScenario, List)` hook keeps its signature** (users subclass the
  executors). That is why the test context initializer travels through the consume-once thread-local
  `TEST_CONTEXT_INITIALIZER` instead of a parameter. Executors running scenarios on another thread must hand it over
  with `takeTestContextInitializer()` (calling thread) and `runWithTestContextInitializer(...)` (executor thread), as
  `AsyncScenarioExecutorService#startScenarioAsync` does.
- **Intermediate messages use the channel path:** `add` → `receive` puts the future into `activeFutures[context]` →
  `send` completes it. In `send` and `fail(context, …)`, `activeFutures` takes precedence over / is handled alongside
  the bound request. Correlation effectively requires `citrus.simulator.mode=async`: in sync mode the first response is
  only returned once the whole scenario has finished, so a client never sends the follow-up.
- New public API on `ScenarioExecutorService` gets a `default` implementation that preserves previous behaviour;
  custom executors exist downstream.

## Citrus behaviour this relies on

Verify against the sources jars in `~/.m2/repository/org/citrusframework/citrus-base/<version>/` before assuming
otherwise:

- `DefaultTestCase#executeAction` wraps any failing action in `TestCaseFailedException`, which `runner.$(...)` /
  `DefaultTestCaseRunner#run` throws out of `scenario.run(runner)`.
- `DefaultTestCase#finish` (via `runner.stop()`) throws `TestCaseFailedException` if exceptions were added to the
  context, e.g. by `SimulatorScenario#registerException`. That exception replaces one thrown from the `try` block.
- `AbstractEndpointAdapter#handleMessage` calls the fallback adapter when the reply is `null` or has no payload.
- `HttpMessageController` answers a `null` reply with the endpoint's default status code (200).

## Testing

- Behaviour must hold in both modes: extend the abstract `SimulatorEndpointAdapterIT` (inherited by
  `SynchronousSimulatorEndpointAdapterIT` and `AsynchronousSimulatorEndpointAdapterIT`) rather than one of them. Use a
  JUnit `@Timeout` to prove that something answers immediately.
- `@Scenario` classes declared in integration tests are registered globally. `ScenarioResourceIT.getTestSimulatorScenario`
  counts scenarios whose name contains "Simulator", so adjust its expected count when adding one.
- Mockito runs with strict stubs: `ScenarioExecutorServiceTest#getSimulatorScenarioMock()` stubs `getScenarioEndpoint()`;
  use a plain `mock(SimulatorScenario.class)` when the scenario never runs.
- `ScenarioEndpoint` concurrency tests (`ScenarioEndpointTest`) run real threads; keep them deterministic with latches.
- Always run the samples after changes here: `./mvnw -pl simulator-spring-boot install` first, then
  `./mvnw -f simulator-samples/pom.xml verify -fae`. They bind port 8080 (check it is free) and cover sync, async,
  correlation, JMS, SOAP faults via fallback adapter (`sample-wsdl`) and 555 assertions (`sample-rest`).
