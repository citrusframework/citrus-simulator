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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Christoph Deppisch
 */
public class HttpRequestMappingKeyExtractor extends AbstractMappingKeyExtractor {

    @Autowired
    private SimulatorConfigurationProperties configuration;

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage) {
            String contextPath =((HttpMessage) request).getContextPath();
            String requestPath = ((HttpMessage) request).getPath();

            String mappingResult = ((contextPath != null && contextPath.startsWith("/") ? contextPath.substring(1) : "") +
                    (requestPath != null && requestPath.startsWith("/") ? requestPath : "/" + requestPath) +
                    "#" + ((HttpMessage) request).getRequestMethod().name()).replaceAll("/", "-");

            if (mappingResult.startsWith("-")) {
                mappingResult = mappingResult.substring(1);
            }

            return mappingResult;
        }

        return configuration.getDefaultScenario();
    }
}
