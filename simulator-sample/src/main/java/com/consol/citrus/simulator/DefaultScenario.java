package com.consol.citrus.simulator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component("Default")
@Scope("prototype")
public class DefaultScenario extends AbstractSimulatorScenario {

    @Override
    protected void configure() {
        sendSOAPResponse()
                .payload("");
    }
}
