package org.citrusframework.simulator.ws;

import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import org.citrusframework.spi.ReferenceResolver;
import org.citrusframework.ws.actions.ReceiveSoapMessageAction;
import org.citrusframework.ws.actions.SendSoapFaultAction;
import org.citrusframework.ws.actions.SendSoapMessageAction;
import org.citrusframework.ws.actions.SoapActionBuilder;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioActionBuilder extends SoapActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Bean reference resolver */
    private ReferenceResolver referenceResolver;

    public SoapScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Sets the bean reference resolver.
     * @param referenceResolver
     */
    public SoapScenarioActionBuilder withReferenceResolver(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
        return this;
    }
    
    /**
     * Default scenario receive operation.
     * @return
     */
    public ReceiveSoapMessageAction.Builder receive() {
        return server(scenarioEndpoint.getName()).withReferenceResolver(referenceResolver).receive().endpoint(scenarioEndpoint);
    }

    /**
     * Default scenario send response operation.
     *
     * @return
     */
    public SendSoapMessageAction.Builder send() {
        return server(scenarioEndpoint.getName()).send().endpoint(scenarioEndpoint);
    }

    /**
     * Sends SOAP fault as scenario response.
     * @return
     */
    public SendSoapFaultAction.Builder sendFault() {
        return server(scenarioEndpoint.getName()).sendFault().endpoint(scenarioEndpoint);
    }
}
