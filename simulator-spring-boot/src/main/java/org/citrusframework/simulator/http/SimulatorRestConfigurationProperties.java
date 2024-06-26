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

package org.citrusframework.simulator.http;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christoph Deppisch
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "citrus.simulator.rest")
public class SimulatorRestConfigurationProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorRestConfigurationProperties.class);

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
     * The OpenApi used by the simulator to simulate OpenApi operations.
     */
    private OpenApi openApi = new OpenApi();

    /**
     * Gets the urlMappings.
     *
     * @return
     */
    @NotNull
    public List<String> getUrlMappings() {
        return urlMappings;
    }

    /**
     * Sets the urlMappings.
     *
     * @param urlMappings
     */
    public void setUrlMappings(List<String> urlMappings) {
        this.urlMappings = urlMappings != null ? unmodifiableList(urlMappings) : emptyList();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Using the simulator configuration: {}", this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(enabled)
            .append(urlMappings)
            .toString();
    }

    @Getter
    @Setter
    public static class OpenApi {
        private String api;
        private String contextPath;
        private boolean enabled = false;
        private String alias;
    }
}
