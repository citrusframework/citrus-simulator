package com.consol.citrus.simulator.ws;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.SoapActionBuilder;
import com.consol.citrus.dsl.builder.SoapClientRequestActionBuilder;
import com.consol.citrus.dsl.builder.SoapClientResponseActionBuilder;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.context.ApplicationContext;

public class SoapClientScenarioRunnerActionBuilder extends SoapActionBuilder {

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

    public SoapClientScenarioRunnerActionBuilder(ScenarioRunner runner, Endpoint endpoint) {
        this.runner = runner;
        this.endpoint = endpoint;
    }

    /**
     * client receive response action.
     *
     * @return
     */
    public TestAction receive(SoapBuilderSupport<SoapClientResponseActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(endpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.client().receive());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * client send request action.
     *
     * @return
     */
    public TestAction send(SoapBuilderSupport<SoapClientRequestActionBuilder> configurer) {
        SoapScenarioActionBuilder builder = new SoapScenarioActionBuilder(endpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.client().send());
        return runner.run(builder.build()).getDelegate();
    }

    @Override
    public SoapClientScenarioRunnerActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapClientScenarioRunnerActionBuilder) super.withApplicationContext(applicationContext);
    }
}
