/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator;

import com.consol.citrus.Citrus;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.config.SimulatorImportSelector;
import com.consol.citrus.simulator.correlation.CorrelationHandlerRegistry;
import com.consol.citrus.simulator.repository.RepositoryConfig;
import com.consol.citrus.simulator.scenario.ScenarioBeanNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Christoph Deppisch
 */
@Configuration
@ComponentScan(basePackages = {
        "com.consol.citrus.simulator.controller",
        "com.consol.citrus.simulator.listener",
        "com.consol.citrus.simulator.service",
}, nameGenerator = ScenarioBeanNameGenerator.class)
@Import(value = {CitrusSpringConfig.class, SimulatorImportSelector.class, RepositoryConfig.class})
@ImportResource(
        locations = {
                "classpath*:citrus-simulator-context.xml",
                "classpath*:META-INF/citrus-simulator-context.xml"
        })
@PropertySource(
        value = {
                "META-INF/citrus-simulator.properties"
        }, ignoreResourceNotFound = true)
@EnableConfigurationProperties(SimulatorConfigurationProperties.class)
@ConditionalOnProperty(prefix = "citrus.simulator", value = "enabled", havingValue = "true", matchIfMissing = true)
public class SimulatorAutoConfiguration {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorAutoConfiguration.class);

    /** Application version */
    private static String version;

    /* Load application version */
    static {
        try (final InputStream in = new ClassPathResource("META-INF/app.version").getInputStream()) {
            Properties versionProperties = new Properties();
            versionProperties.load(in);
            version = versionProperties.get("app.version").toString();
        } catch (IOException e) {
            log.warn("Unable to read application version information", e);
            version = "";
        }
    }

    /**
     * Gets the version.
     *
     * @return
     */
    public static String getVersion() {
        return version;
    }

    @Bean
    public Citrus citrus(ApplicationContext applicationContext) {
        return Citrus.newInstance(applicationContext);
    }

    @Bean
    public CorrelationHandlerRegistry correlationHandlerRegistry() {
        return new CorrelationHandlerRegistry();
    }
}
