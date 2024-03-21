package org.citrusframework.simulator.junit;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

import org.citrusframework.http.client.HttpClient;
import org.citrusframework.http.client.HttpEndpointConfiguration;
import org.junit.jupiter.api.Test;

@TestWithCitrusSimulator
class CitrusSimulatorExtensionIntegrationTest {

    @Test
    void injectRuntimeInfo(CitrusSimulatorContext citrusSimulatorContext) {
        assertThat(citrusSimulatorContext)
            .isNotNull()
            .satisfies(info -> assertThat(info)
                .extracting(CitrusSimulatorContext::getHttpClient)
                .isNotNull()
                .extracting(HttpClient::getEndpointConfiguration)
                .extracting(HttpEndpointConfiguration::getRequestUrl)
                .asString().startsWith("http://localhost:")
                .satisfies(requestUrl -> assertThat(parseInt(requestUrl.split(":")[2]))
                    .isGreaterThan(0)
                )
            );
    }
}
