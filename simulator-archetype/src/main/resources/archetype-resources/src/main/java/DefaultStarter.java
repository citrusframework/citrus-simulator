package ${package};

import com.consol.citrus.simulator.model.AbstractScenarioStarter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Component("DefaultStarter")
@Scope("prototype")
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
