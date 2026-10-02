# Architecture

This document describes how the Citrus Simulator is put together. It is aimed at contributors (human and AI) who need
to find their way around the code base. User-facing documentation lives in [`simulator-docs`](simulator-docs/src/main/asciidoc/index.adoc)
and is published at https://citrusframework.org/citrus-simulator/reference/html/.

## What the simulator is

The Citrus Simulator is a **Spring Boot auto-configuration library** that turns any Spring Boot application into a
simulator for HTTP/REST, SOAP Web Services, JMS and other Citrus-supported transports. Users write *scenarios* using the
[Citrus](https://citrusframework.org) test DSL; incoming requests are mapped to a scenario, the scenario runs like a
Citrus test case, and its `send` actions produce the response. Every execution is recorded in a database and can be
inspected via a REST API and an Angular web UI.

## Modules

| Module                  | Artifact                       | Purpose                                                                                                 |
|-------------------------|--------------------------------|---------------------------------------------------------------------------------------------------------|
| `simulator-spring-boot` | `citrus-spring-boot-simulator` | The library: auto-configuration, transport adapters, scenario engine, persistence, REST API.            |
| `simulator-ui`          | `citrus-simulator-ui`          | Angular SPA (JHipster-generated). Built into `target/classes/static/simulator-ui` and packaged as a jar. |
| `simulator-docs`        | `citrus-simulator-docs`        | AsciiDoc reference documentation.                                                                       |
| `simulator-samples`     | – (not deployed)               | Runnable sample simulators (REST, WS, WSDL, JMS, mail, Swagger/OpenAPI, …) with TestNG Citrus ITs.      |
| `simulator-archetypes`  | `citrus-simulator-archetype-*` | Maven archetypes to bootstrap new simulator projects.                                                   |

A user application depends on `citrus-spring-boot-simulator` (and optionally `citrus-simulator-ui`), adds
`@SpringBootApplication`, and declares scenarios as Spring beans.

## Backend (`simulator-spring-boot`)

Base package: `org.citrusframework.simulator`.

| Package                                       | Responsibility                                                                                                   |
|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| (root) `SimulatorAutoConfiguration`           | Entry point. Component-scans `web.rest`, `listener`, `service`, `endpoint`; creates the `Citrus` instance.        |
| `config`                                      | `SimulatorConfigurationProperties` (`citrus.simulator.*`), `SimulatorConfigurer`, `SimulatorImportSelector`.     |
| `http`                                        | REST transport: auto-configuration, `HttpMessageController` wiring, request-mapping / OpenAPI scenario mappers.   |
| `ws`                                          | SOAP transport: `MessageDispatcherServlet`, SOAP action mapping, WSDL-based scenario generation.                 |
| `jms`                                         | JMS transport: endpoint poller on a JMS destination.                                                              |
| `endpoint`                                    | Transport-agnostic core: `SimulatorEndpointAdapter`, pollers, message interceptors that persist messages.        |
| `scenario`                                    | Scenario API for users: `@Scenario`, `@Starter`, `SimulatorScenario`, `ScenarioRunner`, `ScenarioEndpoint`.      |
| `scenario.mapper`                             | `ScenarioMapper` implementations: extract a scenario name from an inbound message (header, XPath, JsonPath, …).  |
| `correlation`                                 | Correlation handlers that route *intermediate* messages to an already running scenario.                         |
| `service`, `service.impl`                     | Business services (CRUD + query services built on JPA Criteria, `criteria` and `filter` packages).               |
| `service.runner`                              | `ScenarioExecutorService` implementations: `DefaultScenarioExecutorService` (sync), `AsyncScenarioExecutorService`. |
| `listener`                                    | Citrus test listeners that record executions/actions/results (`SimulatorStatusListener`).                       |
| `model`, `repository`                         | JPA entities and Spring Data repositories.                                                                       |
| `web.rest`, `web.rest.dto`, `web.rest.dto.mapper` | REST resources under `/api`, DTOs and MapStruct mappers.                                                     |
| `dictionary`, `template`                      | XML/JSON data dictionaries and payload template helpers.                                                         |

Auto-configurations are registered in
`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
Defaults for Spring properties live in `META-INF/citrus-simulator.properties`.

### Request lifecycle

```
 inbound request (HTTP / SOAP / JMS / channel)
        │
        ▼
 transport adapter ──► SimulatorEndpointAdapter.handleMessageInternal()
                              │
            ┌─────────────────┴──────────────────┐
            │ CorrelationHandlerRegistry matches? │
            └───────┬───────────────────┬────────┘
               yes  │                   │ no
                    ▼                   ▼
   push into running scenario's   ScenarioMapper extracts mapping key
   ScenarioEndpoint               → Spring bean lookup (fallback: citrus.simulator.default-scenario)
                    │                   │
                    │                   ▼
                    │            message + CompletableFuture queued on ScenarioEndpoint
                    │            ScenarioExecutorService.run(scenario, name, params)
                    │                   │
                    │                   ▼
                    │            ScenarioExecution persisted, ScenarioRunner created,
                    │            scenario.run(runner) executes Citrus actions
                    │            (receive() consumes the queued message, send() completes the future)
                    ▼                   ▼
          SimulatorEndpointAdapter awaits future (citrus.simulator.default-timeout)
                              │
                              ▼
                 response returned to the transport
                 (scenario failure → HTTP status 555)
```

Key points:

- **Transports all converge on `SimulatorEndpointAdapter`.** `SimulatorRestAutoConfiguration`,
  `SimulatorWebServiceAutoConfiguration`, `SimulatorJmsAutoConfiguration` and `SimulatorEndpointAutoConfiguration` each
  create their own adapter instance and plug in a transport-specific `ScenarioMapper`.
- **Customisation is via configurer beans.** Users implement `SimulatorRestConfigurer`, `SimulatorWebServiceConfigurer`,
  `SimulatorJmsConfigurer` or `SimulatorEndpointComponentConfigurer`, usually by extending the abstract
  `Simulator*Adapter` classes, to change URL mappings, scenario mappers, fallback adapters, etc.
- **Scenarios are prototype-scoped Spring beans** (`@Scenario` and `@Starter` are meta-annotated with `@Component` and
  `@Scope("prototype")`); the bean name is the scenario name. `@Starter` scenarios (`ScenarioStarter`) are not triggered by messages but started manually via the UI/REST API with
  `ScenarioParameter`s.
- **Execution mode** is selected by `citrus.simulator.mode`: `sync` (default, one scenario at a time) or `async`
  (thread pool of `citrus.simulator.executor-threads`, required for scenarios with intermediate messages).
  In sync mode the scenario has completed when `run()` returns (`ScenarioExecutorService#isSynchronous`), so the
  adapter does not wait for the future and answers "no response" immediately if none was sent. Background and
  follow-up plan: [`SCENARIO_RESPONSE_HANDLING.md`](SCENARIO_RESPONSE_HANDLING.md).

### Persistence and recording

Recording is a side effect of Citrus listeners and interceptors, not of the scenario code:

- `ScenarioExecutionService.createAndSaveExecutionScenario` creates a `ScenarioExecution` before the run and stores its
  id in the Citrus `TestContext` variable `ScenarioExecution.EXECUTION_ID`.
- `SimulatorStatusListener` (Citrus `TestListener`/`TestActionListener`) records `ScenarioAction`s and the final
  `TestResult`. With `citrus.simulator.simulation-results.persist-only-failed-scenarios=true` successful executions are
  deleted instead of completed.
- `EndpointMessageHandler` (invoked from `EndpointConsumerInterceptor`, `EndpointProducerInterceptor` and
  `ScenarioEndpoint`) stores inbound and outbound
  `Message`s and `MessageHeader`s attached to the execution.

Entities: `ScenarioExecution` → `ScenarioAction`, `Message` → `MessageHeader`, `ScenarioParameter`, `TestResult` →
`TestParameter`. A diagram is in `simulator-docs/src/main/asciidoc/images/database-schema.puml`. The database is
whatever `DataSource` the host application provides; samples and tests use embedded H2. `spring.jpa.open-in-view` is
disabled, so lazy associations must be fetched inside service-level transactions.

### REST API

Resources in `web.rest` expose read/query endpoints under `/api/**` (JHipster style: criteria filters such as
`?scenarioName.contains=x`, pagination headers via `PaginationUtil`). `ScenarioResource` lists scenarios/starters and
launches starters. Actuator endpoints are under `/api/manage`. OpenAPI docs are produced by springdoc.

### Enforced layering

`TechnicalStructureTest` (ArchUnit) enforces that:

- the `web` layer is only accessed by `config`;
- the `model` (domain) layer is only accessed by `config`, `dto`, `service`, `repository`, `endpoint`, `listener`,
  `scenario` and `web`.

## Frontend (`simulator-ui`)

Angular 22 SPA, originally generated with JHipster (`generator-jhipster` is a dev dependency; generated idioms are kept).

- `src/main/webapp/app/entities/*` – list/detail views per backend entity (message, scenario-execution, test-result, …).
- `src/main/webapp/app/scenario`, `scenario-result` – scenario catalogue, starter launch with parameters, result views.
- `src/main/webapp/app/home` – dashboard with result summary and "delete all results".
- `src/main/webapp/app/core`, `shared`, `layouts`, `config` – infrastructure (HTTP interceptors, pagination, filters,
  i18n, navbar).
- Unit tests: Jest (`*.spec.ts` next to sources). E2E tests: Playwright in `simulator-ui/tests`.

The build writes to `target/classes/static/simulator-ui`; `citrus-simulator.properties` adds
`classpath:/static/simulator-ui/` to Spring's static resource locations so the UI is served by the simulator itself.
The UI is **skipped by default** in Maven builds (`skipFrontend=true`).

## Samples and tests

- Backend unit tests: JUnit 5 + Mockito + AssertJ, named `*Test`, run by Surefire.
- Backend integration tests: named `*IT`, annotated with `@IntegrationTest` (Spring Boot test against
  `test/TestApplication`), run by Failsafe.
- Samples: each sample is a standalone simulator; its `*IT` classes are TestNG Citrus tests
  (`TestNGCitrusSpringSupport`) run via Failsafe against the embedded simulator. CI runs
  `./mvnw -f simulator-samples/pom.xml verify` after the main build.
