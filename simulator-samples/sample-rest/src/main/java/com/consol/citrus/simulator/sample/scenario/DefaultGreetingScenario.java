package com.consol.citrus.simulator.sample.scenario;

/**
 * @author Christoph Deppisch
 */
public class DefaultGreetingScenario extends AbstractGreetingScenario {

    @Override
    String getGreetingMessage() {
        return "What's up!";
    }
}
