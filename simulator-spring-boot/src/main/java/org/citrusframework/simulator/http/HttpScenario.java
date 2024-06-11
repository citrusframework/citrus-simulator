package org.citrusframework.simulator.http;

import org.citrusframework.simulator.scenario.IdentifiableSimulatorScenario;

public interface HttpScenario extends IdentifiableSimulatorScenario {

    String getPath();

    String getMethod();

}
