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

import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.endpoint.TestExecutingEndpointAdapter;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.config.SimulatorConfiguration;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.service.ActivityService;
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
    private SimulatorConfiguration configuration;

    @Autowired
    private ActivityService activityService;

    @Override
    public Message dispatchMessage(Message request, String mappingName) {
        if (mappingName.equals(SimulatorMappingKeyExtractor.INTERVENING_MESSAGE_MAPPING)) {
            return getResponseEndpointAdapter().handleMessage(request);
        }

        if (getApplicationContext().containsBean(mappingName)) {
            return super.dispatchMessage(request, mappingName);
        } else {
            LOG.info(String.format("Unable to find scenario for mapping '%s' - " +
                    "using default scenario '%s'", mappingName, configuration.getDefaultScenario()));
            return super.dispatchMessage(request, configuration.getDefaultScenario());
        }
    }

    protected void prepareExecution(Message request, TestDesigner testDesigner) {
        recordNewScenarioExecution(testDesigner);
    }

    protected void prepareExecution(Message request, TestRunner testRunner) {
        recordNewScenarioExecution(testRunner);
    }

    private void recordNewScenarioExecution(Object scenario) {
        ScenarioExecution ts = activityService.createExecutionScenario(lookupScenarioName(scenario), Collections.emptyList());

        if (scenario instanceof TestRunner) {
            ((TestRunner) scenario).variable(ScenarioExecution.EXECUTION_ID, ts.getExecutionId());
        } else if (scenario instanceof TestDesigner) {
            ((TestDesigner) scenario).variable(ScenarioExecution.EXECUTION_ID, ts.getExecutionId());
        } else {
            throw new CitrusRuntimeException(String.format(
                    "Received invalid scenario type '%s'. Scenario must be of type %s or %s",
                    scenario.getClass().getName(),
                    TestRunner.class.getName(),
                    TestDesigner.class.getName()
            ));

        }
    }

    private String lookupScenarioName(Object scenario) {
        Scenario scenarioAnnotation = scenario.getClass().getAnnotation(Scenario.class);
        if (scenarioAnnotation != null) {
            return scenarioAnnotation.value();
        } else {
            throw new CitrusRuntimeException(String.format("%s is missing the %s annotation", scenario.getClass().getName(), Scenario.class.getName()));
        }
    }

}
