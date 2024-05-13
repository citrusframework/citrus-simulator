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

package org.citrusframework.simulator.http;

import jakarta.annotation.Nullable;
import lombok.Builder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.ScenarioListAware;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.mapper.AbstractScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.citrusframework.simulator.scenario.ScenarioUtils.getAnnotationFromClassHierarchy;

/**
 * Scenario mapper performs mapping logic on request mapping annotations on given scenarios. Scenarios match on request method as well as
 * request path pattern matching.
 *
 * @author Christoph Deppisch
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class HttpRequestAnnotationScenarioMapper extends AbstractScenarioMapper implements ScenarioListAware {

    private final HttpRequestAnnotationMatcher httpRequestAnnotationMatcher = HttpRequestAnnotationMatcher.instance();

    @Autowired(required = false)
    private @Nullable List<SimulatorScenario> scenarioList;

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage httpMessage) {
            return getMappingKeyForHttpMessage(httpMessage);
        }

        return super.getMappingKey(request);
    }

    protected String getMappingKeyForHttpMessage(HttpMessage httpMessage) {
        List<SimulatorScenario> nullSafeList = Optional.ofNullable(scenarioList).orElse(Collections.emptyList());

        // First look for exact match
        Optional<String> mapping = nullSafeList.stream()
            .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                .scenario(scenario)
                .requestMapping(getAnnotationFromClassHierarchy(scenario, RequestMapping.class))
                .build()
            )
            .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
            .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage, swrm.requestMapping(), true))
            .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage, swrm.requestMapping()))
            .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage, swrm.requestMapping()))
            .map(EnrichedScenarioWithRequestMapping::name)
            .findFirst();

        // If that didn't help, look for inecaxt match
        if (mapping.isEmpty()) {
            mapping = nullSafeList.stream()
                .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                    .scenario(scenario)
                    .requestMapping(AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class))
                    .build()
                )
                .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage, swrm.requestMapping(), false))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage, swrm.requestMapping()))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage, swrm.requestMapping()))
                .map(EnrichedScenarioWithRequestMapping::name)
                .findFirst();
        }

        return mapping.orElseGet(() -> super.getMappingKey(httpMessage));
    }

    /**
     * Gets the scenarios.
     *
     * @return
     */
    public List<SimulatorScenario> getScenarios() {
        return scenarioList;
    }

    /**
     * Sets the scenarios.
     *
     * @param scenarios
     */
    public void setScenarios(List<SimulatorScenario> scenarios) {
        this.scenarioList = scenarios;
    }

    @Override
    public void setScenarioList(List<SimulatorScenario> scenarioList) {
        this.scenarioList = scenarioList;
    }

    /**
     * Gets the configuration.
     *
     * @return
     */
    public SimulatorConfigurationProperties getConfiguration() {
        return super.getSimulatorConfigurationProperties();
    }

    /**
     * Sets the configuration.
     *
     * @param configuration
     */
    public void setConfiguration(SimulatorConfigurationProperties configuration) {
        this.setSimulatorConfigurationProperties(configuration);
    }

    @Builder
    private record EnrichedScenarioWithRequestMapping(SimulatorScenario scenario, RequestMapping requestMapping) {

        public boolean hasRequestMapping() {
            return requestMapping != null;
        }

        public String name() {
            return scenario.getName();
        }
    }
}
