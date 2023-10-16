package org.citrusframework.simulator.ui.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class InfoEndpointConfiguration implements InfoContributor {

    private final Environment environment;

    public InfoEndpointConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("activeProfiles", environment.getActiveProfiles());
    }
}
