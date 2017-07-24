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

package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.dsl.builder.BuilderSupport;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.runner.DefaultTestRunner;
import com.consol.citrus.simulator.correlation.CorrelationBuilderSupport;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import com.consol.citrus.simulator.correlation.StartCorrelationHandlerAction;
import com.consol.citrus.simulator.http.HttpScenarioRunnerActionBuilder;
import com.consol.citrus.simulator.ws.SoapScenarioRunnerActionBuilder;
import com.consol.citrus.ws.actions.SendSoapFaultAction;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class ScenarioRunner extends DefaultTestRunner {

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /**
     * Default constructor using fields.
     *
     * @param scenarioEndpoint
     * @param applicationContext
     * @param context
     */
    public ScenarioRunner(ScenarioEndpoint scenarioEndpoint, ApplicationContext applicationContext, TestContext context) {
        super(applicationContext, context);
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     *
     * @return
     */
    public StartCorrelationHandlerAction correlation(CorrelationBuilderSupport configurer) {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint, getApplicationContext());
        configurer.configure(() -> builder);
        doFinally().actions(builder.stop());
        return run(builder.build());
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public HttpScenarioRunnerActionBuilder http() {
        return new HttpScenarioRunnerActionBuilder(this, scenarioEndpoint)
                .withApplicationContext(getApplicationContext());
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public SoapScenarioRunnerActionBuilder soap() {
        return new SoapScenarioRunnerActionBuilder(this, scenarioEndpoint)
                .withApplicationContext(getApplicationContext());
    }

    /**
     * Sends SOAP fault as scenario response.
     *
     * @return
     */
    public SendSoapFaultAction sendFault(BuilderSupport<SoapServerFaultResponseActionBuilder> configurer) {
        SoapServerFaultResponseActionBuilder actionBuilder = (SoapServerFaultResponseActionBuilder)
                new SoapServerFaultResponseActionBuilder(new DelegatingTestAction<>(), scenarioEndpoint)
                        .withApplicationContext(getApplicationContext())
                        .description("Sending SOAP fault");

        configurer.configure(actionBuilder);

        run(actionBuilder);
        return (SendSoapFaultAction) actionBuilder.build().getDelegate();
    }

    /**
     * Gets the scenario inbound endpoint.
     *
     * @return
     */
    public ScenarioEndpoint scenarioEndpoint() {
        return scenarioEndpoint;
    }

}
