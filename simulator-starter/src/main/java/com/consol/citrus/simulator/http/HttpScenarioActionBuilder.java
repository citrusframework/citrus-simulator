package com.consol.citrus.simulator.http;

import com.consol.citrus.dsl.builder.HttpActionBuilder;
import com.consol.citrus.dsl.builder.HttpServerActionBuilder;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public HttpScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerReceiveActionBuilder receive() {
        return new HttpServerActionBuilder(action, scenarioEndpoint)
                .withApplicationContext(applicationContext)
                .receive();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerSendActionBuilder send() {
        return new HttpServerActionBuilder(action, scenarioEndpoint)
                .withApplicationContext(applicationContext)
                .send();
    }

    @Override
    public HttpScenarioActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (HttpScenarioActionBuilder) super.withApplicationContext(applicationContext);
    }
}
