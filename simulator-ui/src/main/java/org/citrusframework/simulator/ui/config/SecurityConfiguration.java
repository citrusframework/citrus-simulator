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
package org.citrusframework.simulator.ui.config;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ui.filter.SpaWebFilter;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfiguration {

    private final SimulatorUiConfigurationProperties simulatorUiConfigurationProperties;

    private final @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;
    private final @Nullable SimulatorRestAdapter simulatorRestAdapter;

    private final @Nullable SimulatorWebServiceConfigurationProperties  simulatorWebServiceConfigurationProperties;
    private final @Nullable SimulatorWebServiceAdapter simulatorWebServiceAdapter;


    public SecurityConfiguration(
        SimulatorUiConfigurationProperties simulatorUiConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestAdapter simulatorRestAdapter,
        @Autowired(required = false) @Nullable SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorWebServiceAdapter simulatorWebServiceAdapter
        ) {
        this.simulatorUiConfigurationProperties = simulatorUiConfigurationProperties;
        this.simulatorRestConfigurationProperties = simulatorRestConfigurationProperties;
        this.simulatorRestAdapter = simulatorRestAdapter;
        this.simulatorWebServiceConfigurationProperties = simulatorWebServiceConfigurationProperties;
        this.simulatorWebServiceAdapter = simulatorWebServiceAdapter;
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

            .addFilterAfter(new SpaWebFilter(simulationEndpointsRequestMatcher), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(contentSecurity -> contentSecurity.policyDirectives(simulatorUiConfigurationProperties.getSecurity().getContentSecurityPolicy()))
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
            );

        return http.build();
    }

    private RequestMatcher getSimulationEndpointsRequestMatcher() {
        List<String> urlMappings = new ArrayList<>();
        configureRestMatchers(urlMappings);
        configureWebServiceMatchers(urlMappings);

        return new OrRequestMatcher(createMatchers(urlMappings));
    }

    private void configureWebServiceMatchers(List<String> urlMappings) {
        if (!Objects.isNull(simulatorWebServiceConfigurationProperties)
            && !Objects.isNull(simulatorWebServiceAdapter)
            && simulatorWebServiceAdapter.servletMapping(
            simulatorWebServiceConfigurationProperties) != null) {
            urlMappings.add(simulatorWebServiceAdapter.servletMapping(
                simulatorWebServiceConfigurationProperties));
        } else if (!Objects.isNull(simulatorWebServiceConfigurationProperties)
            && simulatorWebServiceConfigurationProperties.getServletMapping() != null) {
            urlMappings.add(simulatorWebServiceConfigurationProperties.getServletMapping());
        }
    }

    private void configureRestMatchers(List<String> urlMappings) {
        if (!Objects.isNull(simulatorRestConfigurationProperties) && !Objects.isNull(simulatorRestAdapter)) {
            urlMappings.addAll(simulatorRestAdapter.urlMappings(
                simulatorRestConfigurationProperties));
        } else if (!Objects.isNull(simulatorRestConfigurationProperties)) {
            urlMappings.addAll(simulatorRestConfigurationProperties.getUrlMappings());
        }
    }

    /**
     * Create request matchers from url mappings.
     * <p>
     * Note that the configured urlMappings for simulator are always absolute. Therefore, we use an
     * {@link AntPathRequestMatcher} for matching.
     */
    private static RequestMatcher[] createMatchers(List<String> urlMappings) {
        List<RequestMatcher> matchers =  urlMappings.stream()
            .map(AntPathRequestMatcher::new)
            .collect(Collectors.toList());

        if (matchers.isEmpty()) {
            matchers.add(new AntPathRequestMatcher("/**/*"));
        }

        return matchers.toArray(new RequestMatcher[0]);
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
