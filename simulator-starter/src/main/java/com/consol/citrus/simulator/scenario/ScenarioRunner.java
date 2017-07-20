package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.dsl.builder.BuilderSupport;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.runner.DefaultTestRunner;
import com.consol.citrus.simulator.correlation.CorrelationHandlerBuilder;
import com.consol.citrus.simulator.correlation.CorrelationManager;
import com.consol.citrus.ws.actions.SendSoapFaultAction;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class ScenarioRunner extends DefaultTestRunner {

    /** Scenario direct endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /**
     * Default constructor using fields.
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
     * @return
     */
    public CorrelationManager correlation() {
        return () -> {
            CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint);
            run(builder);
            doFinally().actions(builder.stop());
            return builder;
        };
    }

    /**
     * Sends SOAP fault as scenario response.
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
     * @return
     */
    public ScenarioEndpoint scenarioEndpoint() {
        return scenarioEndpoint;
    }

}
