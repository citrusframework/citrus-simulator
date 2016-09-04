package ${package};

import com.sun.org.apache.regexp.internal.recompile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.consol.citrus.dsl.design.ExecutableTestDesignerComponent;
import com.consol.citrus.endpoint.Endpoint;

/**
 * @author Christoph Deppisch
 */
@Component("DefaultScenario")
@Scope("prototype")
public class DefaultScenario extends ExecutableTestDesignerComponent {

    @Autowired
    @Qualifier("simInboundEndpoint")
    protected Endpoint simInbound;

    @Override
    protected void configure() {
        echo("Default scenario was triggered");

        receive(simInbound);

        echo("Received SOAP request");
    }
}
