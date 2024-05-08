package org.citrusframework.simulator;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packagesOf = SimulatorAutoConfiguration.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class TechnicalStructureTest {

    @ArchTest
    static final ArchRule respectsTechnicalArchitectureLayers = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Config").definedBy("..config..")
        .layer("Web").definedBy("..web..")
        .layer("DTO").definedBy("..dto..")
        .layer("Service").definedBy("..service..")
        .layer("Persistence").definedBy("..repository..")
        .layer("Domain").definedBy("..model..")
        .layer("Endpoint").definedBy("..endpoint..")
        .layer("Listener").definedBy("..listener..")
        .layer("Scenario").definedBy("..scenario..")

        .whereLayer("Web").mayOnlyBeAccessedByLayers("Config")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Config", "DTO", "Service", "Persistence", "Endpoint", "Listener", "Scenario");
}
