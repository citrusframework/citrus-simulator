package com.consol.citrus.simulator.ws;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.SoapActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerRequestActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerResponseActionBuilder;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.context.ApplicationContext;

public class SoapServerScenarioRunnerActionBuilder extends SoapActionBuilder {

    /**
     * Scenario endpoint
     */
    private final Endpoint endpoint;

    /**
     * Scenario endpoint
     */
    private final ScenarioRunner runner;

    /**
     * Spring application context
     */
    private ApplicationContext applicationContext;

    public SoapServerScenarioRunnerActionBuilder(ScenarioRunner runner, Endpoint endpoint) {
        this.runner = runner;
        this.endpoint = endpoint;
    }

    /**
     * Default scenario receive operation.
     *
     * @return
     */
    public TestAction receive(SoapBuilderSupport<SoapServerRequestActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(endpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.server().receive());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * Default scenario send response operation.
     *
     * @return
     */
    public TestAction send(SoapBuilderSupport<SoapServerResponseActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(endpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.server().send());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * Default scenario send fault operation.
     *
     * @return
     */
    public TestAction sendFault(SoapBuilderSupport<SoapServerFaultResponseActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(endpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.sendFault());
        return runner.run(builder.build()).getDelegate();
    }

    @Override
    public SoapServerScenarioRunnerActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapServerScenarioRunnerActionBuilder) super.withApplicationContext(applicationContext);
    }
}
