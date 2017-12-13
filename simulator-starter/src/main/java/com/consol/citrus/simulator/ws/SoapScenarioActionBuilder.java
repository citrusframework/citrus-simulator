package com.consol.citrus.simulator.ws;

import com.consol.citrus.dsl.builder.*;
import com.consol.citrus.simulator.scenario.ScenarioEndpoint;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioActionBuilder extends SoapActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public SoapScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public SoapServerRequestActionBuilder receive() {
        return new SoapServerActionBuilder(action, scenarioEndpoint)
                .withApplicationContext(applicationContext)
                .receive();
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public SoapServerResponseActionBuilder send() {
        return new SoapServerActionBuilder(action, scenarioEndpoint)
                .withApplicationContext(applicationContext)
                .send();
    }

    /**
     * Sends SOAP fault as scenario response.
     * @return
     */
    public SoapServerFaultResponseActionBuilder sendFault() {
        return new SoapServerActionBuilder(action, scenarioEndpoint)
                .withApplicationContext(applicationContext)
                .sendFault();
    }

    @Override
    public SoapScenarioActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapScenarioActionBuilder) super.withApplicationContext(applicationContext);
    }
}
