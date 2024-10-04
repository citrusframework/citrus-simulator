package org.citrusframework.simulator.config;


import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFeatureConfig {

  public static final String EXPERIMENTAL_SCENARIO_LOADING_AT_RUNTIME_ENABLED = "";

  @Bean
  public Client openFeatureClient() {
    return OpenFeatureAPI.getInstance().getClient();
  }
}
