package org.citrusframework.simulator.junit;

import org.citrusframework.http.client.HttpClient;
import org.citrusframework.http.client.HttpEndpointConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CitrusSimulatorContext {

    private static final CitrusSimulatorContext INSTANCE = new CitrusSimulatorContext();

    private final HttpEndpointConfiguration httpEndpointConfiguration;

    private CitrusSimulatorContext() {
        httpEndpointConfiguration = new HttpEndpointConfiguration();
    }

    public static CitrusSimulatorContext citrusSimulatorContext() {
        return INSTANCE;
    }

    void setPort(int port) {
        httpEndpointConfiguration.setRequestUrl("http://localhost:" + port);
    }

    public HttpClient getHttpClient() {
        return new HttpClient(httpEndpointConfiguration);
    }

    static class Resolver implements ParameterResolver {

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == CitrusSimulatorContext.class;
        }

        @Override
        public CitrusSimulatorContext resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return INSTANCE;
        }
    }
}
