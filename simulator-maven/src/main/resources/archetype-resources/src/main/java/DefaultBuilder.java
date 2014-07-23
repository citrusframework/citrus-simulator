package ${package};

import com.sun.org.apache.regexp.internal.recompile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.consol.citrus.dsl.CitrusTestBuilder;
import com.consol.citrus.endpoint.Endpoint;

/**
 * @author Christoph Deppisch
 */
@Component("DefaultBuilder")
@Scope("prototype")
public class DefaultBuilder extends CitrusTestBuilder {

    @Autowired
    @Qualifier("simInboundEndpoint")
    protected Endpoint simInbound;

    @Override
    protected void configure() {

        echo("Default builder was triggered");

        receive(simInbound);

        echo("Received SOAP request");

    }
}
