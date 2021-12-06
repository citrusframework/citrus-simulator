package org.citrusframework.simulator.http;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import com.consol.citrus.dsl.builder.HttpActionBuilder;

import org.citrusframework.simulator.scenario.ScenarioEndpoint;
import com.consol.citrus.spi.ReferenceResolver;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioActionBuilder extends HttpActionBuilder {

    /** Scenario endpoint */
    private final ScenarioEndpoint scenarioEndpoint;
    
    public HttpScenarioActionBuilder(ScenarioEndpoint scenarioEndpoint) {
        this.scenarioEndpoint = scenarioEndpoint;
    }

    /**
     * Sets the bean reference resolver.
     * @param referenceResolver
     */
    public HttpScenarioActionBuilder withReferenceResolver(ReferenceResolver referenceResolver) {
        super.withReferenceResolver(referenceResolver);
        return this;
    }

    /**
     * Default scenario receive operation.
     * @return
     */
    public HttpServerReceiveActionBuilder receive() {
        HttpServerReceiveActionBuilder receiveActionBuilder = server(scenarioEndpoint).receive();
        return receiveActionBuilder;
    }

    /**
     * Default scenario send response operation.
     * @return
     */
    public HttpServerSendActionBuilder send() {
        HttpServerSendActionBuilder sendActionBuilder = server(scenarioEndpoint).send();
        return sendActionBuilder;
    }

    
    public HttpServerActionBuilder server(ScenarioEndpoint scenarioEndpoint) {
        
        // TODO: change HttpActionBuilder to accept endpoint in calls to server
        // Endpoint cannot be used to create a server anymore although underlying builders 
        // would accept it. Citrus 3.0.0 needs to be adjusted to make this possible again. 
        // For now use an ugly reflection hack to inject the endpoint.
        HttpServerActionBuilder serverActionBuilder = server((String)null);
        Field delegateField = ReflectionUtils.findField(serverActionBuilder.getClass(), "delegate");
        ReflectionUtils.makeAccessible(delegateField);
        Object builderDelegate = ReflectionUtils.getField(delegateField, serverActionBuilder);

        Field httpServerField = ReflectionUtils.findField(builderDelegate.getClass(), "httpServer");
        ReflectionUtils.makeAccessible(httpServerField);
        ReflectionUtils.setField(httpServerField, builderDelegate, scenarioEndpoint);

        return serverActionBuilder;
    }
}
