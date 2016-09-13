package ${package};

import om.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.http.SimulatorRestScenario;

@cScenario("DefaultScenario")
public class DefaultScenario extends SimulatorRestScenario {

    @Override
    protected void configure() {
        echo("Default scenario was started");

        receiveScenarioRequest();

        sendScenarioResponse()
            .payload("OK");

        echo("Received request");
    }
}
