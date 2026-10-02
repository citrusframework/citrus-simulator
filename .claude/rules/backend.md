---
paths:
  - "simulator-spring-boot/**"
  - "simulator-samples/**"
  - "simulator-archetypes/**"
---

# Backend rules

- Constructor injection with `private final` fields; no field `@Autowired` in new code.
- Use SLF4J: `private static final Logger logger = LoggerFactory.getLogger(Foo.class);` (Lombok's `@Slf4j` is configured
  to the same name via `lombok.config`, but only use it where Lombok is already present).
- Services are interfaces in `service` with implementations in `service.impl`; read-side filtering goes through the
  `*QueryService` + `*Criteria` + `service.filter` types, mirroring the existing entities.
- REST resources live in `web.rest`, return DTOs (never JPA entities) and map them with MapStruct mappers in
  `web.rest.dto.mapper`.
- `spring.jpa.open-in-view` is off: initialise lazy associations inside `@Transactional` service methods, not in
  resources or mappers.
- New auto-configuration classes must be registered in
  `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and guarded with
  `@ConditionalOnProperty` / `@ConditionalOnMissingBean` so users can opt out or override.
- Anything that touches scenario execution must work in both `citrus.simulator.mode=sync` and `async`. There are
  paired tests (`SyncScenarioExecutorServiceIT`, `AsyncScenarioExecutorServiceIT`, `*SimulatorEndpointAdapterIT`);
  extend both.
- Unit tests use `@ExtendWith(MockitoExtension.class)`, `@Mock` fields named `*Mock`, the class under test named
  `fixture`, and AssertJ assertions. Integration tests use `@IntegrationTest` and are named `*IT`.
- Sample ITs are TestNG Citrus tests (`TestNGCitrusSpringSupport`, `@CitrusTest`), not JUnit.
- When changing the scenario API (`scenario` package, `ScenarioRunner` DSL), check the samples and archetype templates
  under `simulator-archetypes/*/src/main/resources/archetype-resources` still compile against it.
