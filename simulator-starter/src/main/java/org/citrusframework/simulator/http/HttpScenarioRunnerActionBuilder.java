package org.citrusframework.simulator.http;

import org.citrusframework.TestAction;
import org.citrusframework.http.actions.HttpActionBuilder;
import org.citrusframework.http.actions.HttpServerActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import org.citrusframework.spi.ReferenceResolver;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioRunnerActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Scenario endpoint */
    private final ScenarioRunner runner;

    /** Bean reference resolver */
    private ReferenceResolver referenceResolver;

    public HttpScenarioRunnerActionBuilder(ScenarioRunner runner, ScenarioEndpoint scenarioEndpoint) {
        this.runner = runner;
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public TestAction receive(HttpBuilderSupport<HttpServerActionBuilder.HttpServerReceiveActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.receive());
        return runner.run(builder.build());
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(HttpBuilderSupport<HttpServerActionBuilder.HttpServerSendActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.send());
        return runner.run(builder.build());
    }

    /**
     * Sets the bean reference resolver.
     * @param referenceResolver
     */
    public HttpScenarioRunnerActionBuilder withReferenceResolver(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
        return this;
    }
}
