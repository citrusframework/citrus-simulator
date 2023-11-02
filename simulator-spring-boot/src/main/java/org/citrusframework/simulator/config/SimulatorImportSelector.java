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

package org.citrusframework.simulator.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author Christoph Deppisch
 */
public class SimulatorImportSelector implements DeferredImportSelector, EnvironmentAware {

    private static final String SIMULATOR_CONFIGURATION_CLASS_PROPERTY = "citrus.simulator.configuration.class";
    private static final String SIMULATOR_CONFIGURATION_CLASS_ENV = "CITRUS_SIMULATOR_CONFIGURATION_CLASS";
    private static final String SIMULATOR_CONFIGURATION_CLASS_DEFAULT = "org.citrusframework.simulator.SimulatorConfig";

    /**
     * The Spring application context environment
     */
    private Environment env;

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        String configurationClassName = env.getProperty(SIMULATOR_CONFIGURATION_CLASS_PROPERTY,
                env.getProperty(SIMULATOR_CONFIGURATION_CLASS_ENV, SIMULATOR_CONFIGURATION_CLASS_DEFAULT));

        if (ClassUtils.isPresent(configurationClassName, this.getClass().getClassLoader())) {
            return new String[]{ configurationClassName };
        } else {
            return new String[]{};
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
