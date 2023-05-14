package org.citrusframework.simulator.ws;

import org.citrusframework.TestAction;
import org.citrusframework.http.actions.HttpActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.spi.ReferenceResolver;
import org.citrusframework.ws.actions.ReceiveSoapMessageAction;
import org.citrusframework.ws.actions.SendSoapFaultAction;
import org.citrusframework.ws.actions.SendSoapMessageAction;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioRunnerActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Scenario endpoint */
    private final ScenarioRunner runner;

    /** Bean reference resolver */
    private ReferenceResolver referenceResolver;

    public SoapScenarioRunnerActionBuilder(ScenarioRunner runner, ScenarioEndpoint scenarioEndpoint) {
        this.runner = runner;
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public TestAction receive(SoapBuilderSupport<ReceiveSoapMessageAction.Builder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.receive());
        return runner.run(builder.build());
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(SoapBuilderSupport<SendSoapMessageAction.Builder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.send());
        return runner.run(builder.build());
    }

    /**
     * Default scenario send fault operation.
     * @return
     */
    public TestAction sendFault(SoapBuilderSupport<SendSoapFaultAction.Builder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.sendFault());
        return runner.run(builder.build());
    }
    
    /**
     * Sets the bean reference resolver.
     * @param referenceResolver
     */
    public SoapScenarioRunnerActionBuilder withReferenceResolver(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
        return this;
    }
}
