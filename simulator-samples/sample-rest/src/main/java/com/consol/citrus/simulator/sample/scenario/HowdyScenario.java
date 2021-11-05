package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.simulator.scenario.Scenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Scenario("Howdy")
@RequestMapping(value = "/services/rest/simulator/howdy", method = RequestMethod.POST)
public class HowdyScenario extends DefaultGreetingScenario {

    @Override
    String getGreetingMessage() {
        return "Howdy partner!";
    }
}
