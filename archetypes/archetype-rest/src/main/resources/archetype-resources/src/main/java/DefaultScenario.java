package ${package};

import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.http.SimulatorRestScenario;

@Scenario("DEFAULT_SCENARIO")
public class DefaultScenario extends SimulatorRestScenario {

    @Override
    protected void configure() {
        scenario()
            .receive();

        scenario()
            .send()
            .payload("Simulator is up and running!");
    }
}
