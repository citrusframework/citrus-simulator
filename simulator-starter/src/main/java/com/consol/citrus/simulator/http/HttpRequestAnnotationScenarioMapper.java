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

package com.consol.citrus.simulator.http;

import com.consol.citrus.endpoint.adapter.mapping.AbstractMappingKeyExtractor;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.SimulatorScenario;
import com.consol.citrus.simulator.scenario.mapper.ScenarioMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario mapper performs mapping logic on request mapping annotations on given scenarios. Scenarios match on request method as well as
 * request path pattern matching.
 *
 * @author Christoph Deppisch
 */
public class HttpRequestAnnotationScenarioMapper extends AbstractMappingKeyExtractor implements ScenarioMapper {

    @Autowired(required = false)
    private List<SimulatorScenario> scenarios = new ArrayList<>();

    @Autowired
    private SimulatorConfigurationProperties configuration;

    private HttpRequestAnnotationMatcher httpRequestAnnotationMatcher = HttpRequestAnnotationMatcher.instance();

    /**
     * Request path matcher
     */
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage) {
            return getMappingKeyForHttpMessage((HttpMessage) request);
        }
        return configuration.getDefaultScenario();
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
        return scenarios.stream()
                .map(scenario -> EnrichedScenarioWithRequestMapping.builder()
                        .scenario(scenario)
                        .requestMapping(AnnotationUtils.findAnnotation(scenario.getClass(), RequestMapping.class))
                        .build()
                )
                .filter(EnrichedScenarioWithRequestMapping::hasRequestMapping)
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestPathSupported(httpMessage, swrm.getRequestMapping()))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestMethodSupported(httpMessage, swrm.getRequestMapping()))
                .filter(swrm -> httpRequestAnnotationMatcher.checkRequestQueryParamsSupported(httpMessage, swrm.getRequestMapping()))
                .map(EnrichedScenarioWithRequestMapping::name)
                .findFirst().orElse(configuration.getDefaultScenario());
    }

    /**
     * Gets the pathMatcher.
     *
     * @return
     */
    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    /**
     * Sets the pathMatcher.
     *
     * @param pathMatcher
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Gets the scenarios.
     *
     * @return
     */
    public List<SimulatorScenario> getScenarios() {
        return scenarios;
    }

    /**
     * Sets the scenarios.
     *
     * @param scenarios
     */
    public void setScenarios(List<SimulatorScenario> scenarios) {
        this.scenarios = scenarios;
    }

    /**
     * Gets the configuration.
     *
     * @return
     */
    public SimulatorConfigurationProperties getConfiguration() {
        return configuration;
    }

    /**
     * Sets the configuration.
     *
     * @param configuration
     */
    public void setConfiguration(SimulatorConfigurationProperties configuration) {
        this.configuration = configuration;
    }
}
