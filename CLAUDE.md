# CLAUDE.md

Guidance for Claude Code (and other coding agents) working in this repository.

## Project

Citrus Simulator: a Spring Boot auto-configuration library that simulates HTTP/REST, SOAP, JMS and other endpoints
using Citrus test scenarios, plus an Angular UI to inspect executions. Java 17, Spring Boot 4, Citrus 5, Angular 22.

Read @ARCHITECTURE.md before making non-trivial changes. Project metadata, branch and commit conventions for the
OSS helper tooling live in `.oss-ai-helper-rules/`. Module-specific rules are in `.claude/rules/`.

## Modules

- `simulator-spring-boot` – the library (`org.citrusframework.simulator`). Most changes happen here.
- `simulator-ui` – Angular SPA, served from the library jar's classpath.
- `simulator-samples` – runnable sample simulators with TestNG Citrus integration tests.
- `simulator-archetypes` – Maven archetypes; keep them in sync when the public scenario API changes.
- `simulator-docs` – AsciiDoc user documentation; update it when user-visible behaviour or properties change.

## Commands

Use the Maven wrapper (`./mvnw`, or `mvnw.cmd` on Windows). Run Maven in the module where the change happened and do
not run several Maven builds in parallel.

```shell
./mvnw clean install -DskipTests                     # full build, UI skipped by default
./mvnw clean install -DskipFrontend=false             # include simulator-ui in the build
./mvnw -pl simulator-spring-boot verify               # backend unit (*Test) + integration (*IT) tests
./mvnw -pl simulator-spring-boot test -Dtest=ScenarioResourceTest          # single unit test
./mvnw -pl simulator-spring-boot verify -Dit.test=ScenarioResourceIT -Dtest=none -Dsurefire.failIfNoSpecifiedTests=false   # single IT
./mvnw -f simulator-samples/pom.xml verify            # sample ITs (requires simulator-spring-boot installed first)
./mvnw -pl simulator-samples/sample-rest spring-boot:run   # run a sample simulator on http://localhost:8080
```

Frontend (run inside `simulator-ui/`, Node >= 24):

```shell
npm ci
npm start                 # dev server on :4200, proxies /api and /services to a running simulator on :8080
npm test                  # lint + Jest unit tests
npm run prettier:check    # CI fails on formatting; fix with `npm run prettier:format`
npm run e2e               # Playwright
```

## Rules

- **Public API is used by downstream simulators.** Everything under `scenario`, `config`, the `*Configurer` /
  `*Adapter` classes and `citrus.simulator.*` properties is public API. Do not change signatures or property names
  without justification; keep backwards compatibility.
- **Do not add dependencies** (Maven or npm) without stating why. Versions are managed as properties in the root
  `pom.xml`; use the `/citrus-update-versions` command to align with a Citrus release.
- **Lombok:** do not introduce Lombok into files that do not already use it. Records are fine for new internal types;
  do not convert existing public classes to records.
- **Respect the layering** enforced by `TechnicalStructureTest` (ArchUnit): nothing except `config` may depend on `web`.
- **New `citrus.simulator.*` properties** go into `SimulatorConfigurationProperties` (or the transport-specific
  properties class), get a default, and are documented in `simulator-docs`.
- **Tests are required** for behaviour changes. Unit tests: JUnit 5 + Mockito + AssertJ, `*Test`. Spring context
  tests: `*IT` with `@IntegrationTest`. Follow the existing test of the class you touch.
- **New source files** need the Apache 2.0 license header used throughout the repository (copy it from a neighbour).
- Follow `.editorconfig`: LF line endings, 4-space indent for Java/XML, 2-space for TS/JSON/YAML/HTML/SCSS.
- Do not edit generated or build output (`target/`, `node_modules/`, `simulator-ui/target/classes/static`).

## Git

- Branch from `main`. Commit messages follow Conventional Commits, referencing the issue where there is one:
  `fix(#425): implement bulk delete for TestResultService`, `feat: persist only failed scenarios`,
  `chore(simulator-ui): update dependencies`.
- Contributions require a DCO sign-off (`git commit -s`), see `simulator-docs/contributing.md`.
- Do not commit, push or open pull requests unless asked.
