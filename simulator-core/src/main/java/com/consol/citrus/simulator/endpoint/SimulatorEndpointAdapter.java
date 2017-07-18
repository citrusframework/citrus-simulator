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

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.dsl.endpoint.TestExecutingEndpointAdapter;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.service.ScenarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Christoph Deppisch
 */
@Component
public class SimulatorEndpointAdapter extends TestExecutingEndpointAdapter {

    /**
     * Logger
     */
    private static Logger LOG = LoggerFactory.getLogger(SimulatorEndpointAdapter.class);

    @Autowired
    private SimulatorConfigurationProperties configuration;

    @Autowired
    private ScenarioService scenarioService;

    @Override
    public Message dispatchMessage(Message request, String mappingName) {
        if (mappingName.equals(SimulatorMappingKeyExtractor.INTERVENING_MESSAGE_MAPPING)) {
            return getResponseEndpointAdapter().handleMessage(request);
        }

        if (getApplicationContext().containsBean(mappingName)) {
            scenarioService.run(mappingName, Collections.EMPTY_LIST);
        } else {
            LOG.info(String.format("Unable to find scenario for mapping '%s' - " +
                    "using default scenario '%s'", mappingName, configuration.getDefaultScenario()));
            scenarioService.run(configuration.getDefaultScenario(), Collections.EMPTY_LIST);
        }

        return getResponseEndpointAdapter().handleMessage(request);
    }

}
