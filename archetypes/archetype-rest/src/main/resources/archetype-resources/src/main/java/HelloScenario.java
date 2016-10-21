package ${package};

import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.http.SimulatorRestScenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.GET)
public class HelloScenario extends SimulatorRestScenario {

    @Override
    protected void configure() {
        scenario()
            .receive();

        scenario()
            .send()
            .payload("Hello User!");
    }
}
