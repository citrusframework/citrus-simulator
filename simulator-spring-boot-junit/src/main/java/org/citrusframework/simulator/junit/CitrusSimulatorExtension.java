/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.junit;

import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.citrusframework.simulator.junit.CitrusSimulatorContext.citrusSimulatorContext;

public class CitrusSimulatorExtension implements AfterAllCallback, BeforeAllCallback {

    private static ConfigurableApplicationContext context;

    private static int randomPort;

    private static synchronized void startContextGracefully(Optional<TestWithCitrusSimulator> testWithCitrusSimulator) {
        if (isNull(context)) {
            var application = new SpringApplication(DefaultSimulatorApplication.class);
            application.setDefaultProperties(
                Map.of(
                    "citrus.simulator.enabled", true,
                    "server.port", "0"
                )
            );

            testWithCitrusSimulator.ifPresent(configuration -> application.setSources(new HashSet<>(asList(configuration.scenarioPackages()))));

            context = application.run();

            randomPort = parseInt(requireNonNull(context.getEnvironment().getProperty("local.server.port")));
            citrusSimulatorContext().setPort(randomPort);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        var testWithCitrusSimulator = context.getTestClass()
            .flatMap(testClass -> Optional.ofNullable(testClass.getAnnotation(TestWithCitrusSimulator.class)));

        startContextGracefully(testWithCitrusSimulator);

        testWithCitrusSimulator.ifPresent(CitrusSimulatorExtension::injectUrlIntoProperties);

        if (testWithCitrusSimulator.map(TestWithCitrusSimulator::disableSimulatorForMainThread).orElse(true)) {
            var autoConfigurationClass = SimulatorAutoConfiguration.class;
            setProperty("spring.autoconfigure.exclude", autoConfigurationClass.getPackageName() + "." + autoConfigurationClass.getSimpleName());
        }
    }

    private static void injectUrlIntoProperties(TestWithCitrusSimulator configuration) {
        asList(configuration.urlProperties()).forEach(property -> setProperty(property, "http://localhost:" + randomPort));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (nonNull(context)) {
            context.close();
        }
    }

    @SpringBootApplication
    static class DefaultSimulatorApplication {
    }
}
