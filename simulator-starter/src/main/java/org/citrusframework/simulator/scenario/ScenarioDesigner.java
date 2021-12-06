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

import org.citrusframework.simulator.correlation.CorrelationHandlerBuilder;
import org.citrusframework.simulator.correlation.CorrelationManager;
import org.citrusframework.simulator.http.HttpScenarioActionBuilder;
import org.citrusframework.simulator.ws.SoapScenarioActionBuilder;
import org.springframework.context.ApplicationContext;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.ReceiveMessageActionBuilder;
import com.consol.citrus.dsl.builder.SendMessageActionBuilder;
import com.consol.citrus.dsl.design.DefaultTestDesigner;
import com.consol.citrus.spi.ReferenceResolver;

/**
 * @author Christoph Deppisch
 */
public class ScenarioDesigner extends DefaultTestDesigner {

    /**
     * Scenario direct endpoint
     */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring bean application context */
    private final ApplicationContext applicationContext;
    
    /** Bean reference resolver */
    private final ReferenceResolver referenceResolver;

    /**
     * Default constructor using fields.
     *
     * @param scenarioEndpoint
     * @param referenceResolver
     * @param context
     */
    public ScenarioDesigner(ScenarioEndpoint scenarioEndpoint, ReferenceResolver referenceResolver, ApplicationContext applicationContext, TestContext context) {
        super(context);
        this.scenarioEndpoint = scenarioEndpoint;
        this.applicationContext = applicationContext;
        this.referenceResolver = referenceResolver;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     *
     * @return
     */
    public CorrelationManager correlation() {
        return () -> {
            CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint, applicationContext);
            action(builder);
            doFinally().actions(builder.stop());
            return builder;
        };
    }

    /**
     * Receive message from scenario endpoint.
     *
     * @return
     */
    public ReceiveMessageActionBuilder<?> receive() {
        return receive(scenarioEndpoint)
                .description("Receive scenario request");
    }

    /**
     * Send message from scenario endpoint.
     *
     * @return
     */
    public SendMessageActionBuilder<?> send() {
        return send(scenarioEndpoint)
                .description("Send scenario response");
    }

    @Override
    public HttpScenarioActionBuilder http() {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        action(builder);
        return builder;
    }

    @Override
    public SoapScenarioActionBuilder soap() {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        action(builder);
        return builder;
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
