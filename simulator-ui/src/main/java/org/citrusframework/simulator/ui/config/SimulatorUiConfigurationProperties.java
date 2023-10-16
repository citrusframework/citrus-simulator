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

        private static final String DEFAULT_CONTENT_SECURITY_POLICY = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

        private String contentSecurityPolicy = DEFAULT_CONTENT_SECURITY_POLICY;

        public String getContentSecurityPolicy() {
            return contentSecurityPolicy;
        }

        public void setContentSecurityPolicy(String contentSecurityPolicy) {
            this.contentSecurityPolicy = contentSecurityPolicy;
        }
    }
}
