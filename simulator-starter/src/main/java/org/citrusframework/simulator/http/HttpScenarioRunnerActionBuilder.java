package org.citrusframework.simulator.http;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.HttpActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.simulator.scenario.ScenarioRunner;
import com.consol.citrus.spi.ReferenceResolver;

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
    public TestAction receive(HttpBuilderSupport<HttpServerReceiveActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.receive());
        return runner.run(builder.build()).build();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public TestAction send(HttpBuilderSupport<HttpServerSendActionBuilder> configurer) {
        HttpScenarioActionBuilder builder = new HttpScenarioActionBuilder(scenarioEndpoint).withReferenceResolver(referenceResolver);
        configurer.configure(builder.send());
        return runner.run(builder.build()).build();
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
