package org.citrusframework.simulator.http;

import org.citrusframework.http.actions.HttpActionBuilder;
import org.citrusframework.http.actions.HttpServerActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.spi.ReferenceResolver;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    public HttpScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Sets the bean reference resolver.
     * @param referenceResolver
     */
    public HttpScenarioActionBuilder withReferenceResolver(ReferenceResolver referenceResolver) {
        super.withReferenceResolver(referenceResolver);
        return this;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerReceiveActionBuilder receive() {
        return server(scenarioEndpoint).receive();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public HttpServerActionBuilder.HttpServerSendActionBuilder send() {
        return server(scenarioEndpoint).send();
    }
}
