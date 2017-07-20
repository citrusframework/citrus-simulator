package com.consol.citrus.simulator.scenario;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.dsl.builder.BuilderSupport;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.runner.DefaultTestRunner;
import com.consol.citrus.simulator.correlation.*;
import com.consol.citrus.simulator.http.HttpScenarioRunnerActionBuilder;
import com.consol.citrus.simulator.ws.SoapScenarioRunnerActionBuilder;
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
    public StartCorrelationHandlerAction correlation(CorrelationBuilderSupport configurer) {
        CorrelationHandlerBuilder builder = new CorrelationHandlerBuilder(scenarioEndpoint);
        configurer.configure(() -> builder);
        doFinally().actions(builder.stop());
        return run(builder.build());
    }

    /**
     * Special scenario endpoint http operation.
     * @return
     */
    public HttpScenarioRunnerActionBuilder http() {
        return new HttpScenarioRunnerActionBuilder(this, scenarioEndpoint)
                .withApplicationContext(getApplicationContext());
    }

    /**
     * Special scenario endpoint http operation.
     * @return
     */
    public SoapScenarioRunnerActionBuilder soap() {
        return new SoapScenarioRunnerActionBuilder(this, scenarioEndpoint)
                .withApplicationContext(getApplicationContext());
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
