/*
 * Copyright 2006-2016 the original author or authors.
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
import org.springframework.context.annotation.*;

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
}, nameGenerator = ScenarioBeanNameGenerator.class)
@Import(value = { CitrusSpringConfig.class, SimulatorImportSelector.class })
@ImportResource(locations = "classpath*:citrus-simulator-context.xml")
@PropertySource(value = "classpath*:citrus-simulator.properties", ignoreResourceNotFound = true)
public class SimulatorSupport {
}
