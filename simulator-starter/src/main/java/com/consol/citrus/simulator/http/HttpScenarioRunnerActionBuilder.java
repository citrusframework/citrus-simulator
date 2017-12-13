package com.consol.citrus.simulator.http;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.*;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioRunnerActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Scenario endpoint */
    private final ScenarioRunner runner;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public HttpScenarioRunnerActionBuilder(ScenarioRunner runner, ScenarioEndpoint scenarioEndpoint) {
        this.runner = runner;
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public TestAction receive(HttpBuilderSupport<HttpServerActionBuilder.HttpServerReceiveActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.receive());
        return runner.run(builder.build()).getDelegate();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(HttpBuilderSupport<HttpServerActionBuilder.HttpServerSendActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint)
                .withApplicationContext(applicationContext);
        configurer.configure(builder.send());
        return runner.run(builder.build()).getDelegate();
    }

    @Override
    public HttpScenarioRunnerActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (HttpScenarioRunnerActionBuilder) super.withApplicationContext(applicationContext);
    }
}
