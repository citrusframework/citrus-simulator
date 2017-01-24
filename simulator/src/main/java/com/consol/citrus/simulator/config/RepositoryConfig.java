package com.consol.citrus.simulator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for configuring spring data and JPA
 */
@Configuration
@EnableJpaRepositories("com.consol.citrus.simulator.core.repository")
@EntityScan({"com.consol.citrus.simulator.model"})
public class RepositoryConfig {
}
