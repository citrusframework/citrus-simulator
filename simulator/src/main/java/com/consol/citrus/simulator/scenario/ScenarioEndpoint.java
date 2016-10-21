package com.consol.citrus.simulator.scenario;

import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;

/**
 * @author Christoph Deppisch
 */
public interface ScenarioEndpoint {

    /**
     * Receives simulator scenario request.
     * @return
     */
    ReceiveMessageBuilder receive();

    /**
     * Sends simulator scenario response.
     * @return
     */
    SendMessageBuilder send();
}
