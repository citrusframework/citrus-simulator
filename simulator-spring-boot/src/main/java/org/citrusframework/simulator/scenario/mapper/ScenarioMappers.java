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

package org.citrusframework.simulator.scenario.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationPropertiesAware;
import org.citrusframework.simulator.http.HttpOperationScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioListAware;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

/**
 * Scenario mapper chain goes through a list of mappers to find best match of extracted mapping keys. When no suitable
 * mapping key is found in the list of mappers a default mapping is used based on provided base class evaluation.
 *
 * @author Christoph Deppisch
 */
public class ScenarioMappers extends AbstractScenarioMapper implements ScenarioListAware, InitializingBean {

    @Autowired(required = false)
    private List<SimulatorScenario> scenarioList = new ArrayList<>();

    private final List<ScenarioMapper> scenarioMapperList;

    /**
     * Constructor using list of scenario mappers to chain when extracting mapping keys.
     * @param scenarioMapperList
     */
    private ScenarioMappers(ScenarioMapper... scenarioMapperList) {
        this.scenarioMapperList = Arrays.asList(scenarioMapperList);
    }

    /**
     * Creates proper
     * @param scenarioMappers
     * @return
     */
    public static ScenarioMappers of(ScenarioMapper ... scenarioMappers) {
        return new ScenarioMappers(scenarioMappers);
    }

    @Override
    public String getMappingKey(Message message) {
        return scenarioMapperList.stream()
                .map(mapper -> {
                    if (mapper instanceof AbstractScenarioMapper abstractScenarioMapper) {
                        abstractScenarioMapper.setUseDefaultMapping(false);
                    }

                    try {
                        return Optional.ofNullable(mapper.extractMappingKey(message));
                    } catch (Exception e) {
                        return Optional.<String>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(StringUtils::hasLength)
                .filter(key -> scenarioList.parallelStream()
                        .anyMatch(scenario -> {
                            if (scenario instanceof HttpOperationScenario httpOperationScenario) {
                                return key.equals(httpOperationScenario.getScenarioId());
                            }

                            return Optional.ofNullable(AnnotationUtils.findAnnotation(scenario.getClass(), Scenario.class))
                                                                        .map(Scenario::value)
                                                                        .orElse("")
                                                                        .equals(key);
                        }))
                .findFirst()
                .orElseGet(() -> super.getMappingKey(message));
    }

    @Override
    public void afterPropertiesSet() {
        scenarioMapperList.stream()
                .filter(ScenarioListAware.class::isInstance)
                .map(ScenarioListAware.class::cast)
                .forEach(mapper -> mapper.setScenarioList(scenarioList));

        scenarioMapperList.stream()
                .filter(SimulatorConfigurationPropertiesAware.class::isInstance)
                .map(SimulatorConfigurationPropertiesAware.class::cast)
                .forEach(mapper -> mapper.setSimulatorConfigurationProperties(getSimulatorConfigurationProperties()));
    }

    @Override
    public void setScenarioList(List<SimulatorScenario> scenarioList) {
        this.scenarioList = scenarioList;
    }
}
