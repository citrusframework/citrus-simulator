package com.consol.citrus.simulator.scenario;

import com.consol.citrus.simulator.AbstractSimulatorScenario;
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
