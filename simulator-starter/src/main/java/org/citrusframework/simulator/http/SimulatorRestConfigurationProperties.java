/*
 * Copyright 2023 the original author or authors.
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
package org.citrusframework.simulator.http;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator.rest")
public class SimulatorRestConfigurationProperties implements EnvironmentAware {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(SimulatorRestConfigurationProperties.class);

    /**
     * System property constants and environment variable names. Post construct callback reads these values and overwrites
     * settings in this property class in order to add support for environment variables.
     */
    private static final String SIMULATOR_URL_MAPPING_PROPERTY = "citrus.simulator.rest.url.mapping";
    private static final String SIMULATOR_URL_MAPPING_ENV = "CITRUS_SIMULATOR_REST_URL_MAPPING";

    /**
     * Global option to enable/disable REST support, default is true.
     */
    private boolean enabled = true;

    /**
     * The web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     */
    private List<String> urlMappings =  List.of("/services/rest/**");

    /**
     * The Spring application context environment auto-injected by environment aware mechanism.
     */
    private Environment env;

    @PostConstruct
    private void configure() {
        String urlMapping = env.getProperty(SIMULATOR_URL_MAPPING_PROPERTY, env.getProperty(SIMULATOR_URL_MAPPING_ENV, ""));

        if (!urlMapping.isEmpty()) {
            urlMappings = List.of(urlMapping.split(","));
        }

        log.info("Using the simulator configuration: {}", this);
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
     * Gets the urlMapping.
     *
     * @return
     */
    @NotNull
    public List<String> getUrlMappings() {
        return urlMappings;
    }

    /**
     * Sets the urlMapping.
     *
     * @param urlMappings
     */
    public void setUrlMappings(List<String> urlMappings) {
        this.urlMappings = urlMappings != null? Collections.unmodifiableList(urlMappings) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled='" + enabled + '\'' +
                ", urlMappings='" + String.join(",",urlMappings) + '\'' +
                '}';
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
