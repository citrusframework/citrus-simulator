/*
 * Copyright 2006-2016 the original author or authors.
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
import com.consol.citrus.simulator.config.SimulatorConfiguration;
import com.consol.citrus.simulator.scenario.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class AnnotationRequestMappingKeyExtractor extends AbstractMappingKeyExtractor {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(AnnotationRequestMappingKeyExtractor.class);

    @Autowired(required = false)
    private List<SimulatorRestScenario> scenarios = new ArrayList<>();

    @Autowired
    private SimulatorConfiguration configuration;

    @Override
    protected String getMappingKey(Message request) {
        if (request instanceof HttpMessage) {
            String requestPath = ((HttpMessage) request).getPath();

            for (SimulatorRestScenario scenario : scenarios) {
                if (scenario.getClass().getAnnotation(RequestMapping.class) != null) {
                    RequestMapping requestMapping = scenario.getClass().getAnnotation(RequestMapping.class);

                    for (String mappingPath : requestMapping.value()) {
                        if (mappingPath.equals(requestPath)) {
                            if (requestMapping.method().length > 0) {
                                for (RequestMethod method : requestMapping.method()) {
                                    if (method.name().equals(((HttpMessage) request).getRequestMethod().name())) {
                                        return scenario.getClass().getAnnotation(Scenario.class).value();
                                    }
                                }
                            } else {
                                return scenario.getClass().getAnnotation(Scenario.class).value();
                            }
                        }
                    }
                }
            }
        }

        return configuration.getDefaultScenario();
    }
}
