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

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Scenario mapper supports path pattern matching on request path.
 *
 * @author Christoph Deppisch
 */
public class HttpRequestPathScenarioMapper extends AbstractScenarioMapper implements
    ScenarioListAware,
    ApplicationContextAware, ApplicationListener<ScenariosReloadedEvent> {

    private final List<HttpScenario> scenarioList = new CopyOnWriteArrayList<>();

    private ApplicationContext applicationContext;

    /**
     * Request path matcher
     */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage httpMessage) {
            String requestPath = httpMessage.getPath();
            String requestMethod =
                httpMessage.getRequestMethod() != null ? httpMessage.getRequestMethod().name()
                    : null;

            if (requestPath != null && requestMethod != null) {
                for (HttpScenario scenario : scenarioList) {
                    if (requestMethod.equals(scenario.getMethod()) && pathMatcher.match(
                        scenario.getPath(), requestPath)) {
                        return scenario.getScenarioId();
                    }
                }
            }

        }

        return super.getMappingKey(request);
    }

    @PostConstruct
    public void init() {
        setScenariosFromApplicationContext();
    }

    /**
     * Gets the httpScenarios.
     *
     * @return
     */
    public List<HttpScenario> getHttpScenarios() {
        return new ArrayList<>(scenarioList);
    }

    /**
     * Sets the httpScenarios.
     *
     * @param httpScenarios
     */
    public void setHttpScenarios(List<HttpScenario> httpScenarios) {
        updateScenarioList(httpScenarios);
    }

    @Override
    public void setScenarioList(List<SimulatorScenario> scenarioList) {
        updateScenarioList(filterScenarios(scenarioList));
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
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull ScenariosReloadedEvent event) {
        setScenariosFromApplicationContext();
    }

    private void setScenariosFromApplicationContext() {
        updateScenarioList(
            filterScenarios(applicationContext.getBeansOfType(SimulatorScenario.class).values()));
    }

    private List<HttpScenario> filterScenarios(Collection<SimulatorScenario> allScenarios) {
        return allScenarios.stream().filter(HttpScenario.class::isInstance)
            .map(HttpScenario.class::cast)
            .sorted(new HttpPathSpecificityComparator())
            .toList();
    }

    private void updateScenarioList(Collection<HttpScenario> newScenarios) {
        synchronized (this.scenarioList) {
            scenarioList.clear();
            scenarioList.addAll(newScenarios);
        }
    }

}
