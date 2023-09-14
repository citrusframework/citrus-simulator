package org.citrusframework.simulator.http;

import org.citrusframework.http.actions.HttpActionBuilder;
import org.citrusframework.http.actions.HttpServerActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;

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
