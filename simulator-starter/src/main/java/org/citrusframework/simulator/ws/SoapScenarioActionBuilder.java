package org.citrusframework.simulator.ws;

import com.consol.citrus.dsl.builder.ReceiveSoapMessageActionBuilder;
import com.consol.citrus.dsl.builder.SendSoapFaultActionBuilder;
import com.consol.citrus.dsl.builder.SendSoapMessageActionBuilder;
import com.consol.citrus.dsl.builder.SoapActionBuilder;
import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.spi.ReferenceResolver;

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
    public ReceiveSoapMessageActionBuilder receive() {
        return server(scenarioEndpoint.getName()).withReferenceResolver(referenceResolver).receive().endpoint(scenarioEndpoint);
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public SendSoapMessageActionBuilder send() {
        return server(scenarioEndpoint.getName()).send().endpoint(scenarioEndpoint);
    }

    /**
     * Sends SOAP fault as scenario response.
     * @return
     */
    public SendSoapFaultActionBuilder sendFault() {
        return server(scenarioEndpoint.getName()).sendFault().endpoint(scenarioEndpoint);
    }
}
