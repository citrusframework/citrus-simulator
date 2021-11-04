package org.citrusframework.simulator.sample.scenario;

import org.citrusframework.simulator.scenario.Scenario;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class HelloScenario extends AbstractGreetingScenario {

    @Override
    String getGreetingMessage() {
        return "Hi there!";
    }
}
