package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.design.DefaultTestDesigner;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import com.consol.citrus.simulator.correlation.CorrelationManager;
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
     * Sends SOAP fault as scenario response.
     * @return
     */
    public SoapServerFaultResponseActionBuilder sendFault() {
        SoapServerFaultResponseActionBuilder actionBuilder = (SoapServerFaultResponseActionBuilder)
                new SoapServerFaultResponseActionBuilder(new DelegatingTestAction<>(), scenarioEndpoint)
                        .withApplicationContext(getApplicationContext())
                        .description("Sending SOAP fault");

        action(actionBuilder);
        return actionBuilder;
    }

    /**
     * Gets the scenario inbound endpoint.
     * @return
     */
    public ScenarioEndpoint inboundEndpoint() {
        return scenarioEndpoint;
    }

    /**
     * Gets the scenario reply endpoint.
     * @return
     */
    public ScenarioEndpoint replyEndpoint() {
        return scenarioEndpoint;
    }
}
