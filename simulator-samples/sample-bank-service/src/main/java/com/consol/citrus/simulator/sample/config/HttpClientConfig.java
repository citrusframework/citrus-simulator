/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.sample.config;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class HttpClientConfig {

    @Value("${citrus.simulator.defaultTimeout}")
    private long defaultTimeout;

    @Value("${server.port}")
    private int port;

    @Value("${server.ssl.key-store}")
    private URL keyStore;

    @Value("${server.ssl.key-password}")
    private String keyPassword;

    @Bean(name = "simulatorHttpClientEndpoint")
    public HttpClient clientEndpoint() {
        return CitrusEndpoints.http()
                .client()
                .timeout(defaultTimeout)
                .requestUrl(String.format("https://localhost:%s/", port))
                .requestFactory(sslRequestFactory())
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient() {
        try {
            //new ClassPathResource("").getURL()
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(keyStore, keyPassword.toCharArray(),
                            new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslcontext, NoopHostnameVerifier.INSTANCE);

            return HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new BeanCreationException("Failed to create http client for ssl connection", e);
        }
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory sslRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

}
