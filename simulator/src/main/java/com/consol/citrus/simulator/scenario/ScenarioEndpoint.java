package com.consol.citrus.simulator.scenario;

import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;

/**
 * @author Christoph Deppisch
 */
public interface ScenarioEndpoint {

    /**
     * Receives simulator scenario request.
     *
     * @return
     */
    ReceiveMessageBuilder receive();

    /**
     * Receives simulator scenario request.
     *
     * @return
     */
    ReceiveMessageBuilder receive(String endpointName);

    /**
     * Sends simulator scenario response.
     *
     * @return
     */
    SendMessageBuilder send();

    /**
     * Sends simulator scenario response.
     *
     * @return
     */
    SendMessageBuilder send(String endpointName);
}
