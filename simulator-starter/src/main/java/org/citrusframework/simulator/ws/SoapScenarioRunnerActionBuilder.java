package org.citrusframework.simulator.ws;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.*;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import com.consol.citrus.spi.ReferenceResolver;

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
    public TestAction receive(SoapBuilderSupport<ReceiveSoapMessageActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.receive());
        return runner.run(builder.build()).build();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(SoapBuilderSupport<SendSoapMessageActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.send());
        return runner.run(builder.build()).build();
    }

    /**
     * Default scenario send fault operation.
     * @return
     */
    public TestAction sendFault(SoapBuilderSupport<SendSoapFaultActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.sendFault());
        return runner.run(builder.build()).build();
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
