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

import org.citrusframework.endpoint.EndpointAdapter;
import org.citrusframework.endpoint.adapter.EmptyResponseEndpointAdapter;
import org.citrusframework.http.interceptor.LoggingHandlerInterceptor;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorRestAdapter implements SimulatorRestConfigurer {

    @Override
    public ScenarioMapper scenarioMapper() {
        return new HttpRequestAnnotationScenarioMapper();
    }

    @Override
    public HandlerInterceptor[] interceptors() {
        return new HandlerInterceptor[]{new LoggingHandlerInterceptor()};
    }

    @Override
    public List<String> urlMappings(SimulatorRestConfigurationProperties simulatorRestConfiguration) {
        return simulatorRestConfiguration.getUrlMappings();
    }

    @Override
    public EndpointAdapter fallbackEndpointAdapter() {
        return new EmptyResponseEndpointAdapter();
    }

    @Override
    public Long exceptionDelay(SimulatorConfigurationProperties simulatorConfiguration) {
        return simulatorConfiguration.getExceptionDelay();
    }
}
