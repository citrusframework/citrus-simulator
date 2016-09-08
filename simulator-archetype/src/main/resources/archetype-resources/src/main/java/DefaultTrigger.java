package ${package};

import com.consol.citrus.simulator.model.AbstractUseCaseTrigger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Component("DefaultTrigger")
@Scope("prototype")
public class DefaultTrigger extends AbstractUseCaseTrigger {

    @Override
    protected void configure() {
        echo("Default trigger was executed");
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
