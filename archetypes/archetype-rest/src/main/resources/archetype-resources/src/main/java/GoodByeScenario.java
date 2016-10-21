package ${package};

import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.http.SimulatorRestScenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Scenario("GoodBye")
@RequestMapping(value = "/services/rest/simulator/goodbye", method = RequestMethod.GET)
public class GoodByeScenario extends SimulatorRestScenario {

    @Override
    protected void configure() {
        scenario()
            .receive();

        scenario()
            .send()
            .payload("GoodBye User!");
    }
}
