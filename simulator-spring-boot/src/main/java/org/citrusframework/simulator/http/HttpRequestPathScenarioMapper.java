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

import java.util.Objects;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.ScenarioListAware;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.citrusframework.simulator.scenario.mapper.AbstractScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario mapper supports path pattern matching on request path.
 *
 * @author Christoph Deppisch
 */
public class HttpRequestPathScenarioMapper extends AbstractScenarioMapper implements ScenarioListAware {

    @Autowired(required = false)
    private List<HttpOperationScenario> scenarioList = new ArrayList<>();

    /** Request path matcher */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage httpMessage) {
            String requestPath = httpMessage.getPath();

            if (requestPath != null) {
                for (HttpOperationScenario scenario : scenarioList) {
                    if (Objects.equals(scenario.getMethod(), ((HttpMessage) request).getRequestMethod().name()) && Objects.equals(requestPath, scenario.getPath())) {
                        return scenario.getScenarioId();
                    }
                }

                for (HttpOperationScenario scenario : scenarioList) {
                    if (Objects.equals(scenario.getMethod(), ((HttpMessage) request).getRequestMethod().name()) && pathMatcher.match(scenario.getPath(), requestPath)) {
                        return scenario.getScenarioId();
                    }
                }
            }
        }

        return super.getMappingKey(request);
    }

    /**
     * Gets the httpScenarios.
     *
     * @return
     */
    public List<HttpOperationScenario> getHttpScenarios() {
        return scenarioList;
    }

    /**
     * Sets the httpScenarios.
     *
     * @param httpScenarios
     */
    public void setHttpScenarios(List<HttpOperationScenario> httpScenarios) {
        this.scenarioList = httpScenarios;
    }

    @Override
    public void setScenarioList(List<SimulatorScenario> scenarioList) {
        this.scenarioList = scenarioList.stream()
                                        .filter(HttpOperationScenario.class::isInstance)
                                        .map(HttpOperationScenario.class::cast)
                                        .toList();
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
