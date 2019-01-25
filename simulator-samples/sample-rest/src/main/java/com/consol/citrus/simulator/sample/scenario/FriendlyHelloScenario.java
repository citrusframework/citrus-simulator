package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.simulator.scenario.Scenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class FriendlyHelloScenario extends HelloScenario{

    @Override
    String getMessage() {
        return "there";
    }
}
