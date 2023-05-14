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

import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.TestActionContainerBuilder;
import org.citrusframework.container.TestActionContainer;
import org.citrusframework.simulator.correlation.CorrelationBuilderSupport;
import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.citrusframework.simulator.correlation.StartCorrelationHandlerAction;
import org.citrusframework.simulator.http.HttpScenarioRunnerActionBuilder;
import org.citrusframework.simulator.ws.SoapBuilderSupport;
import org.citrusframework.simulator.ws.SoapScenarioRunnerActionBuilder;
import org.citrusframework.ws.actions.SoapActionBuilder;
import org.springframework.context.ApplicationContext;

import org.citrusframework.context.TestContext;
import org.citrusframework.ws.actions.SendSoapFaultAction;

import java.util.Stack;

import static org.citrusframework.container.FinallySequence.Builder.doFinally;

/**
 * @author Christoph Deppisch
 */
public class ScenarioRunner extends DefaultTestCaseRunner {

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring bean application context */
    private ApplicationContext applicationContext;

    // TODO: Is this still relevant?
    /** Optional stack of containers cached for execution */
    protected Stack<TestActionContainerBuilder<? extends TestActionContainer, ?>> containers = new Stack<>();

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
    public StartCorrelationHandlerAction correlation(CorrelationBuilderSupport configurer) {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint, applicationContext);
        configurer.configure(() -> builder);
        // TODO: Not sure if this works? Where does this action get called?
        // See: https://github.com/citrusframework/citrus/pull/946/files#diff-f5bc91f18a5e506ee478ab30a349ef1aee9eecf68efbb8954c5978a7eb14029eL749
        doFinally().actions(builder.stop());
        return run(builder);
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public HttpScenarioRunnerActionBuilder http() {
        return new HttpScenarioRunnerActionBuilder(this, scenarioEndpoint).withReferenceResolver(getContext().getReferenceResolver());
    }

    /**
     * Special scenario endpoint http operation.
     *
     * @return
     */
    public SoapScenarioRunnerActionBuilder soap() {
        return new SoapScenarioRunnerActionBuilder(this, scenarioEndpoint).withReferenceResolver(getContext().getReferenceResolver());
    }

    /**
     * Sends SOAP fault as scenario response.
     *
     * @return
     */
    public SendSoapFaultAction sendFault(SoapBuilderSupport<SendSoapFaultAction.Builder> configurer) {
        SendSoapFaultAction.Builder sendFaultActionBuilder = new SoapActionBuilder().withReferenceResolver(getContext().getReferenceResolver())
            .server(scenarioEndpoint.getName())
                .sendFault();
    
        configurer.configure(sendFaultActionBuilder);
        run(sendFaultActionBuilder);
    
        return sendFaultActionBuilder.build();
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
