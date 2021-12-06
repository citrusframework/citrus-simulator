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

package org.citrusframework.simulator.scenario;

import org.citrusframework.simulator.correlation.CorrelationBuilderSupport;
import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.citrusframework.simulator.http.HttpScenarioRunnerActionBuilder;
import org.citrusframework.simulator.ws.SoapScenarioRunnerActionBuilder;
import org.springframework.context.ApplicationContext;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.BuilderSupport;
import com.consol.citrus.dsl.builder.SendSoapFaultActionBuilder;
import com.consol.citrus.dsl.builder.SoapActionBuilder;
import com.consol.citrus.dsl.runner.DefaultTestRunner;
import com.consol.citrus.ws.actions.SendSoapFaultAction;

/**
 * @author Christoph Deppisch
 */
public class ScenarioRunner extends DefaultTestRunner {

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring bean application context */
    private ApplicationContext applicationContext;

    /**
     * Default constructor using fields.
     *
     * @param scenarioEndpoint
     * @param applicationContext
     * @param context
     */
    public ScenarioRunner(ScenarioEndpoint scenarioEndpoint, ApplicationContext applicationContext, TestContext context) {
        super(context);
        this.scenarioEndpoint = scenarioEndpoint;
        this.applicationContext = applicationContext;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     *
     * @return
     */
    public CorrelationHandlerBuilder correlation(CorrelationBuilderSupport configurer) {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint, applicationContext);
        configurer.configure(() -> builder);
        doFinally().actions(builder.stop());
        return run(builder);
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public HttpScenarioRunnerActionBuilder http() {
        return new HttpScenarioRunnerActionBuilder(this, scenarioEndpoint).withReferenceResolver(getTestContext().getReferenceResolver());
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public SoapScenarioRunnerActionBuilder soap() {
        return new SoapScenarioRunnerActionBuilder(this, scenarioEndpoint).withReferenceResolver(getTestContext().getReferenceResolver());
    }

    /**
     * Sends SOAP fault as scenario response.
     *
     * @return
     */
    public SendSoapFaultAction sendFault(BuilderSupport<SendSoapFaultActionBuilder> configurer) {
    
        SendSoapFaultActionBuilder sendFaultActionBuilder = new SoapActionBuilder().withReferenceResolver(getTestContext().getReferenceResolver())
            .server(scenarioEndpoint.getName()).sendFault();
    
        configurer.configure(sendFaultActionBuilder);
        run(sendFaultActionBuilder);
    
        return (SendSoapFaultAction) sendFaultActionBuilder.build();
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
