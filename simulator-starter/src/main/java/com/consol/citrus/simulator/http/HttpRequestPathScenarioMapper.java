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

import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.scenario.mapper.ScenarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Special request annotation based scenario mapper supports path pattern matching on request path.
 * As fallback uses default request mapping annotation based mapping.
 *
 * @author Christoph Deppisch
 */
public class HttpRequestPathScenarioMapper extends HttpRequestAnnotationScenarioMapper implements ScenarioMapper {

    @Autowired(required = false)
    private List<HttpOperationScenario> httpScenarios = new ArrayList<>();

    /** Request path matcher */
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage) {
            String requestPath = ((HttpMessage) request).getPath();

            if (requestPath != null) {
                for (HttpOperationScenario scenario : httpScenarios) {
                    if (scenario.getPath().equals(requestPath)) {
                        if (scenario.getMethod().name().equals(((HttpMessage) request).getRequestMethod().name())) {
                            return scenario.getOperation().getOperationId();
                        }
                    }
                }

                for (HttpOperationScenario scenario : httpScenarios) {
                    if (pathMatcher.match(scenario.getPath(), requestPath)) {
                        if (scenario.getMethod().name().equals(((HttpMessage) request).getRequestMethod().name())) {
                            return scenario.getOperation().getOperationId();
                        }
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
        return httpScenarios;
    }

    /**
     * Sets the httpScenarios.
     *
     * @param httpScenarios
     */
    public void setHttpScenarios(List<HttpOperationScenario> httpScenarios) {
        this.httpScenarios = httpScenarios;
    }
}
