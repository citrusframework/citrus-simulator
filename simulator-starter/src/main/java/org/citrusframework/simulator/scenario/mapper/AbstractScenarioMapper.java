/*
 * Copyright 2006-2019 the original author or authors.
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

package org.citrusframework.simulator.scenario.mapper;

import com.consol.citrus.endpoint.adapter.mapping.AbstractMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.config.SimulatorConfigurationPropertiesAware;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract scenario mapper is able to map to default scenario based on autowired or provided simulator properties if enabled. If default mapping
 * is disabled the mapping evaluation raises some exception that should be handled by calling mapping strategies.
 *
 * Subclasses may overwrite mapping key evaluation with custom logic and fallback to this base method implementation.
 *
 * @author Christoph Deppisch
 */
public abstract class AbstractScenarioMapper extends AbstractMappingKeyExtractor implements ScenarioMapper, SimulatorConfigurationPropertiesAware {

    @Autowired
    private SimulatorConfigurationProperties properties;

    /** Should use default mapping as fallback */
    private boolean useDefaultMapping = true;

    @Override
    protected String getMappingKey(Message request) {
        if (properties != null && useDefaultMapping) {
            return properties.getDefaultScenario();
        } else {
            throw new CitrusRuntimeException("Failed to get mapping key");
        }
    }

    @Override
    public void setSimulatorConfigurationProperties(SimulatorConfigurationProperties properties) {
        this.properties = properties;
    }

    public SimulatorConfigurationProperties getSimulatorConfigurationProperties() {
        return this.properties;
    }

    /**
     * Specifies the useDefaultMapping.
     *
     * @param useDefaultMapping
     */
    public void setUseDefaultMapping(boolean useDefaultMapping) {
        this.useDefaultMapping = useDefaultMapping;
    }

    /**
     * Obtains the useDefaultMapping.
     *
     * @return
     */
    public boolean isUseDefaultMapping() {
        return useDefaultMapping;
    }
}
