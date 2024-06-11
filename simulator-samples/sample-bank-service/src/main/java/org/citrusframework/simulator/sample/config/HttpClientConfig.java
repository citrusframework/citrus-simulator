/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.simulator.sample.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static java.lang.String.format;

@Configuration
public class HttpClientConfig {

    private final SimulatorConfigurationProperties simulatorConfigurationProperties;

    @Value("${server.port}")
    private int port;

    @Value("${server.ssl.key-store}")
    private URL keyStore;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    public HttpClientConfig(SimulatorConfigurationProperties simulatorConfigurationProperties) {
        this.simulatorConfigurationProperties = simulatorConfigurationProperties;
    }

    @Bean
    public HttpClient simulatorHttpClientEndpoint() {
        return CitrusEndpoints.http()
            .client()
            .timeout(simulatorConfigurationProperties.getDefaultTimeout())
            .requestUrl(format("https://localhost:%s/", port))
            .requestFactory(sslRequestFactory())
            .build();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        try {
            SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(keyStore, keyStorePassword.toCharArray(),
                    new TrustSelfSignedStrategy())
                .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslcontext, NoopHostnameVerifier.INSTANCE);

            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

            return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException |
                 KeyManagementException e) {
            throw new BeanCreationException("Failed to create http client for ssl connection", e);
        }
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory sslRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }
}
