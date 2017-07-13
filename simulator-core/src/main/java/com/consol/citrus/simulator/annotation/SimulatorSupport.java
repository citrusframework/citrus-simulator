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

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.simulator.bean.ScenarioBeanNameGenerator;
import com.consol.citrus.simulator.config.SimulatorImportSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        "com.consol.citrus.simulator.config",
        "com.consol.citrus.simulator.listener",
        "com.consol.citrus.simulator.service",
        "com.consol.citrus.simulator.endpoint",
        "com.consol.citrus.simulator.web",
        "com.consol.citrus.simulator.controller",
}, nameGenerator = ScenarioBeanNameGenerator.class)
@Import(value = {CitrusSpringConfig.class, SimulatorImportSelector.class})
@ImportResource(
        locations = {
                "classpath*:citrus-simulator-context.xml",
                "classpath*:META-INF/citrus-simulator-context.xml"
        })
@PropertySource(
        value = {
                "citrus-simulator.properties",
                "META-INF/citrus-simulator.properties"
        }, ignoreResourceNotFound = true)
public class SimulatorSupport {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SimulatorSupport.class);

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
}
