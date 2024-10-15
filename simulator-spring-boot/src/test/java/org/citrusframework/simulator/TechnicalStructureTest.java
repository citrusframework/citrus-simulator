/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Config", "DTO", "Service", "Persistence", "Endpoint", "Listener", "Scenario", "Web");
}
