package ${package};

import com.consol.citrus.simulator.scenario.AbstractScenarioStarter;
import com.consol.citrus.simulator.scenario.Starter;

import java.util.*;

@Starter("DefaultStarter")
public class DefaultStarter extends AbstractScenarioStarter {

    @Override
    protected void configure() {
        echo("Default starter was executed");
    }

    @Override
    public String getDisplayName() {
        return "Default";
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
