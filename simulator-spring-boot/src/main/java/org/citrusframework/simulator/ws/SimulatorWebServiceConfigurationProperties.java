/*
 * Copyright 2023-2024 the original author or authors.
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

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator.ws")
public class SimulatorWebServiceConfigurationProperties {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(SimulatorWebServiceConfigurationProperties.class);

    /**
     * Global option to enable/disable SOAP web service support, default is false.
     */
    private boolean enabled;

    /**
     * The web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     */
    private List<String> servletMappings = singletonList("/services/ws/*");

    @PostConstruct
    private void loadProperties() {
        logger.info("Using the simulator configuration: {}", this);
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
     * Gets the servletMapping.
     *
     * @return
     */
    public List<String> getServletMappings() {
        return servletMappings;
    }

    /**
     * Sets the servletMapping.
     *
     * @param servletMappings
     */
    public void setServletMappings(List<String> servletMappings) {
        this.servletMappings = servletMappings;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled='" + enabled + '\'' +
                ", servletMappings=" + servletMappings +
                '}';
    }
}
