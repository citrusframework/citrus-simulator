package com.consol.citrus.simulator.ws;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.*;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioRunnerActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Scenario endpoint */
    private final ScenarioRunner runner;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public SoapScenarioRunnerActionBuilder(ScenarioRunner runner, ScenarioEndpoint scenarioEndpoint) {
        this.runner = runner;
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public TestAction receive(SoapBuilderSupport<SoapServerRequestActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.receive());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(SoapBuilderSupport<SoapServerResponseActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.send());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * Default scenario send fault operation.
     * @return
     */
    public TestAction sendFault(SoapBuilderSupport<SoapServerFaultResponseActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.sendFault());
        return runner.run(builder.build()).getDelegate();
    }

    @Override
    public SoapScenarioRunnerActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapScenarioRunnerActionBuilder) super.withApplicationContext(applicationContext);
    }
}
