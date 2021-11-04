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

package org.citrusframework.simulator.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author Martin Maher
 */
@ConfigurationProperties(prefix = "citrus.simulator.ws.client")
public class SimulatorWebServiceClientConfigurationProperties implements EnvironmentAware {
    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(SimulatorWebServiceClientConfigurationProperties.class);

    /**
     * System property constants and environment variable names. Post construct callback reads these values and overwrites
     * settings in this property class in order to add support for environment variables.
     */
    private static final String SIMULATOR_WSCLIENT_REQUEST_URL_PROPERTY = "citrus.simulator.ws.client.request.url";
    private static final String SIMULATOR_WSCLIENT_REQUEST_URL_ENV = "SIMULATOR_WS_CLIENT_REQUEST_URL";

    /**
     * Global option to enable/disable SOAP web service client support, default is false.
     */
    private boolean enabled;

    /**
     * SOAP server endpoint URL. This is where the SOAP client sends its requests to.
     */
    private String requestUrl = "http://localhost:8080/services/ws/simulator";

    /**
     * The Spring application context environment auto injected by environment aware mechanism.
     */
    private Environment env;

    @PostConstruct
    private void loadProperties() {
        requestUrl = env.getProperty(SIMULATOR_WSCLIENT_REQUEST_URL_PROPERTY,
                env.getProperty(SIMULATOR_WSCLIENT_REQUEST_URL_ENV, requestUrl)
        );

        log.info("Using the simulator configuration: {}", this.toString());
    }

    /**
     * Gets the enabled.
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the request Url.
     *
     * @return
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Sets the request Url.
     *
     * @param requestUrl
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled='" + enabled + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
