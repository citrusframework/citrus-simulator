package org.citrusframework.simulator.ws;

import org.citrusframework.simulator.scenario.ScenarioEndpoint;
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

    public SoapScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public ReceiveSoapMessageAction.Builder receive() {
        return server(scenarioEndpoint.getName()).receive().endpoint(scenarioEndpoint);
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
