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

package org.citrusframework.simulator.ui.config;

import static java.util.Objects.nonNull;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ui.filter.SpaWebFilter;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfiguration {

    private final @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;
    private final @Nullable SimulatorRestAdapter simulatorRestAdapter;

    private final @Nullable SimulatorWebServiceConfigurationProperties  simulatorWebServiceConfigurationProperties;
    private final @Nullable SimulatorWebServiceAdapter simulatorWebServiceAdapter;

    private final String contentSecurityPolicy;

    private final String actuatorPath;
    private final String h2ConsolePath;

    public SecurityConfiguration(
        SimulatorUiConfigurationProperties simulatorUiConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestAdapter simulatorRestAdapter,
        @Autowired(required = false) @Nullable SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorWebServiceAdapter simulatorWebServiceAdapter,
        @Value("${management.endpoints.web.base-path:/api/manage}") String actuatorPath,
        @Value("${spring.h2.console.path:/h2-console}") String h2ConsolePath
    ) {
        this.simulatorRestConfigurationProperties = simulatorRestConfigurationProperties;
        this.simulatorRestAdapter = simulatorRestAdapter;
        this.simulatorWebServiceConfigurationProperties = simulatorWebServiceConfigurationProperties;
        this.simulatorWebServiceAdapter = simulatorWebServiceAdapter;

        this.contentSecurityPolicy = simulatorUiConfigurationProperties.getSecurity().getContentSecurityPolicy();

        this.actuatorPath = actuatorPath;
        this.h2ConsolePath = h2ConsolePath;
    }

    /**
     * Create request matchers from url mappings.
     * <p>
     * Note that the configured urlMappings for simulator are always absolute. Therefore, we use an
     * {@link AntPathRequestMatcher} for matching.
     */
    private static AntPathRequestMatcher[] createMatchers(List<String> urlMappings) {
        List<AntPathRequestMatcher> matchers =  urlMappings.stream()
            .map(AntPathRequestMatcher::new)
            .toList();

        if (matchers.isEmpty()) {
            matchers.add(new AntPathRequestMatcher("/**/*"));
        }

        return matchers.toArray(new AntPathRequestMatcher[0]);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestMatcher simulationEndpointsRequestMatcher = getSimulationEndpointsRequestMatcher();

        http
            .cors(AbstractHttpConfigurer::disable)

            // TODO: https://github.com/citrusframework/citrus-simulator/issues/21
            // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // See https://stackoverflow.com/q/74447118/65681
            // .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            // .ignoringRequestMatchers(simulationEndpointsRequestMatcher)
            .csrf(AbstractHttpConfigurer::disable)

            .addFilterAfter(new SpaWebFilter(actuatorPath, h2ConsolePath, simulationEndpointsRequestMatcher), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(contentSecurity -> contentSecurity.policyDirectives(contentSecurityPolicy))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                authz
                    .anyRequest().permitAll()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.websecurity", name = "debug", havingValue = "true")
    public WebSecurityCustomizer debuggingWebSecurityCustomizer() {
        return web -> web.debug(true);
    }

    private RequestMatcher getSimulationEndpointsRequestMatcher() {
        List<String> urlMappings = new ArrayList<>();

        addRestMatchers(urlMappings);
        addWebServiceMatchers(urlMappings);

        return new OrRequestMatcher(createMatchers(urlMappings));
    }

    private void addWebServiceMatchers(List<String> urlMappings) {
        if (nonNull(simulatorWebServiceConfigurationProperties)
            && nonNull(simulatorWebServiceAdapter)
            && simulatorWebServiceAdapter.servletMappings(simulatorWebServiceConfigurationProperties) != null) {
            urlMappings.addAll(simulatorWebServiceAdapter.servletMappings(simulatorWebServiceConfigurationProperties));
        } else if (nonNull(simulatorWebServiceConfigurationProperties)
            && simulatorWebServiceConfigurationProperties.getServletMappings() != null) {
            urlMappings.addAll(simulatorWebServiceConfigurationProperties.getServletMappings());
        }
    }

    private void addRestMatchers(List<String> urlMappings) {
        if (nonNull(simulatorRestConfigurationProperties)
            && nonNull(simulatorRestAdapter)) {
            urlMappings.addAll(simulatorRestAdapter.urlMappings(simulatorRestConfigurationProperties));
        } else if (nonNull(simulatorRestConfigurationProperties)) {
            urlMappings.addAll(simulatorRestConfigurationProperties.getUrlMappings());
        }
    }
}
