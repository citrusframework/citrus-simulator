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

import static org.citrusframework.simulator.scenario.ScenarioUtils.getAnnotationFromClassHierarchy;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Builder;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.events.ScenariosReloadedEvent;
import org.citrusframework.simulator.scenario.ScenarioListAware;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.mapper.AbstractScenarioMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Scenario mapper performs mapping logic on request mapping annotations on given scenarios.
 * Scenarios match on request method as well as request path pattern matching.
 *
 * @author Christoph Deppisch
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class HttpRequestAnnotationScenarioMapper extends AbstractScenarioMapper implements
    ScenarioListAware,
    ApplicationContextAware, ApplicationListener<ScenariosReloadedEvent> {

    private final HttpRequestAnnotationMatcher httpRequestAnnotationMatcher = HttpRequestAnnotationMatcher.instance();

    private final List<SimulatorScenario> scenarioList = new CopyOnWriteArrayList<>();

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        setScenariosFromApplicationContext();
    }

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage httpMessage) {
            return getMappingKeyForHttpMessage(httpMessage);
        }

        return super.getMappingKey(request);
    }

    protected String getMappingKeyForHttpMessage(HttpMessage httpMessage) {

        // First look for exact match
        Optional<String> mapping = scenarioList.stream()
            .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                .scenario(scenario)
                .requestMapping(getAnnotationFromClassHierarchy(scenario, RequestMapping.class))
                .build()
            )
            .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
            .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage,
                swrm.requestMapping(), true))
            .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage,
                swrm.requestMapping()))
            .filter(
                swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage,
                    swrm.requestMapping()))
            .map(EnrichedScenarioWithRequestMapping::name)
            .findFirst();

        // If that didn't help, look for inecaxt match
        if (mapping.isEmpty()) {
            mapping = scenarioList.stream()
                .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                    .scenario(scenario)
                    .requestMapping(
                        AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class))
                    .build()
                )
                .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage,
                    swrm.requestMapping(), false))
                .filter(
                    swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage,
                        swrm.requestMapping()))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(
                    httpMessage, swrm.requestMapping()))
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
        return new ArrayList<>(scenarioList);
    }

    /**
     * Sets the scenarios.
     *
     * @param scenarios
     */
    public void setScenarios(List<SimulatorScenario> scenarios) {
        updateScenarioList(scenarios);
    }

    @Override
    public void setScenarioList(List<SimulatorScenario> scenarioList) {
        updateScenarioList(scenarioList);
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

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull ScenariosReloadedEvent event) {
       setScenariosFromApplicationContext();
    }

    private void setScenariosFromApplicationContext() {
        updateScenarioList(
            applicationContext.getBeansOfType(SimulatorScenario.class).values().stream()
                .toList());
    }

    private void updateScenarioList(List<SimulatorScenario> newScenarios) {
        synchronized (this.scenarioList) {
            scenarioList.clear();
            scenarioList.addAll(newScenarios);
        }
    }

    @Builder
    private record EnrichedScenarioWithRequestMapping(SimulatorScenario scenario,
                                                      RequestMapping requestMapping) {

        public boolean hasRequestMapping() {
            return requestMapping != null;
        }

        public String name() {
            return scenario.getName();
        }
    }


}
