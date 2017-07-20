package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;
import com.consol.citrus.dsl.design.DefaultTestDesigner;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import com.consol.citrus.simulator.correlation.CorrelationManager;
import com.consol.citrus.simulator.http.HttpScenarioActionBuilder;
import com.consol.citrus.simulator.ws.SoapScenarioActionBuilder;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class ScenarioDesigner extends DefaultTestDesigner {

    /** Scenario direct endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /**
     * Default constructor using fields.
     * @param scenarioEndpoint
     * @param applicationContext
     * @param context
     */
    public ScenarioDesigner(ScenarioEndpoint scenarioEndpoint, ApplicationContext applicationContext, TestContext context) {
        super(applicationContext, context);
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Start new message correlation so scenario is provided with additional inbound messages.
     * @return
     */
    public CorrelationManager correlation() {
        return () -> {
            CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint);
            action(builder);
            doFinally().actions(builder.stop());
            return builder;
        };
    }

    /**
     * Receive message from scenario endpoint.
     * @return
     */
    public ReceiveMessageBuilder receive() {
        return (ReceiveMessageBuilder) receive(scenarioEndpoint)
                .description("Receive scenario request");
    }

    /**
     * Send message from scenario endpoint.
     * @return
     */
    public SendMessageBuilder send() {
        return (SendMessageBuilder) send(scenarioEndpoint)
                .description("Send scenario response");
    }

    @Override
    public HttpScenarioActionBuilder http() {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(getApplicationContext());

        action(builder);
        return builder;
    }

    @Override
    public SoapScenarioActionBuilder soap() {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(getApplicationContext());

        action(builder);
        return builder;
    }

    /**
     * Gets the scenario inbound endpoint.
     * @return
     */
    public ScenarioEndpoint scenarioEndpoint() {
        return scenarioEndpoint;
    }
}
