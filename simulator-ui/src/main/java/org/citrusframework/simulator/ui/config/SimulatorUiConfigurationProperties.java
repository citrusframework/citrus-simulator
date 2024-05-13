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

package org.citrusframework.simulator.ui.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to Citrus Simulator UI.
 *
 * <p> Properties are configured in the application.yml file. </p>
 * <p> This class would also load properties from the Spring Environment, based on the {@code git.properties} and {@code META-INF/build-info.properties} files, if they are found in the classpath.</p>
 */
@Configuration
@ConfigurationProperties(prefix = "org.citrusframework.simulator.ui", ignoreUnknownFields = false)
public class SimulatorUiConfigurationProperties {

    private Security security = new Security();


    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Security {

        private static final String DEFAULT_CONTENT_SECURITY_POLICY = "default-src 'self'; frame-src 'self' data:;";
        private String contentSecurityPolicy = DEFAULT_CONTENT_SECURITY_POLICY;

        public String getContentSecurityPolicy() {
            return contentSecurityPolicy;
        }

        public void setContentSecurityPolicy(String contentSecurityPolicy) {
            this.contentSecurityPolicy = contentSecurityPolicy;
        }
    }
}
