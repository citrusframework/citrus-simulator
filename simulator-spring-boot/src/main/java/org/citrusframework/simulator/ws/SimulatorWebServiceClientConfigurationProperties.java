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

package org.citrusframework.simulator.ws;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Martin Maher
 */
@ConfigurationProperties(prefix = "citrus.simulator.ws.client")
public class SimulatorWebServiceClientConfigurationProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorWebServiceClientConfigurationProperties.class);

    /**
     * Global option to enable/disable SOAP web service client support, default is false.
     */
    private boolean enabled = false;

    /**
     * SOAP server endpoint URL. This is where the SOAP client sends its requests to.
     */
    private String requestUrl = "http://localhost:8080/services/ws/simulator";

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
    public void afterPropertiesSet() {
        logger.info("Using the simulator configuration: {}", this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(enabled)
            .append(requestUrl)
            .toString();
    }
}
