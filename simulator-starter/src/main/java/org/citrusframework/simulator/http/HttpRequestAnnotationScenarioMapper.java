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

package org.citrusframework.simulator.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.ScenarioListAware;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.mapper.AbstractScenarioMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Scenario mapper performs mapping logic on request mapping annotations on given scenarios. Scenarios match on request method as well as
 * request path pattern matching.
 *
 * @author Christoph Deppisch
 */
public class HttpRequestAnnotationScenarioMapper extends AbstractScenarioMapper implements ScenarioListAware {

    @Autowired(required = false)
    private List<SimulatorScenario> scenarioList = new ArrayList<>();

    private HttpRequestAnnotationMatcher httpRequestAnnotationMatcher = HttpRequestAnnotationMatcher.instance();

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage) {
            return getMappingKeyForHttpMessage((HttpMessage) request);
        }

        return super.getMappingKey(request);
    }

    @Data
    @Builder
    private static final class EnrichedScenarioWithRequestMapping {
        final SimulatorScenario scenario;
        final RequestMapping requestMapping;

        public boolean hasRequestMapping() {
            return requestMapping != null;
        }

        public String name() {
            return scenario.getClass().getAnnotation(Scenario.class).value();
        }
    }

    protected String getMappingKeyForHttpMessage(HttpMessage httpMessage) {
        Optional<String> mapping = scenarioList.stream()
                .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                        .scenario(scenario)
                        .requestMapping(AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class))
                        .build()
                )
                .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage, swrm.getRequestMapping(), true))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage, swrm.getRequestMapping()))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage, swrm.getRequestMapping()))
                .map(EnrichedScenarioWithRequestMapping::name)
                .findFirst();

        if (!mapping.isPresent()) {
            mapping = scenarioList.stream()
                    .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                            .scenario(scenario)
                            .requestMapping(AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class))
                            .build()
                    )
                    .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
                    .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage, swrm.getRequestMapping(), false))
                    .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage, swrm.getRequestMapping()))
                    .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage, swrm.getRequestMapping()))
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
}
