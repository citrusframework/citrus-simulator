package org.citrusframework.simulator.ui.config;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServlet;
import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ui.filter.SpaWebFilter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class SecurityConfiguration {

    private final SimulatorUiConfigurationProperties simulatorUiConfigurationProperties;

    private final @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;
    private final @Nullable SimulatorRestAdapter simulatorRestAdapter;
    private final @Nullable SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties;
    private final @Nullable ServletRegistrationBean<? extends HttpServlet> simulatorServletRegistrationBean;

    public SecurityConfiguration(
        SimulatorUiConfigurationProperties simulatorUiConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties,
        @Autowired(required = false) @Nullable SimulatorRestAdapter simulatorRestAdapter,
        @Autowired(required = false) @Nullable SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties,
        @Autowired(required = false) @Nullable @Qualifier("simulatorServletRegistrationBean") ServletRegistrationBean<? extends HttpServlet> simulatorServletRegistrationBean) {
        this.simulatorUiConfigurationProperties = simulatorUiConfigurationProperties;

        this.simulatorRestConfigurationProperties = simulatorRestConfigurationProperties;
        this.simulatorRestAdapter = simulatorRestAdapter;
        this.simulatorWebServiceConfigurationProperties = simulatorWebServiceConfigurationProperties;
        this.simulatorServletRegistrationBean = simulatorServletRegistrationBean;
    }

    private static void addPathWithinApplicationAwareServletMatchers(MvcRequestMatcher.Builder mvc, String urlMapping, List<RequestMatcher> requestMatchers) {
        String pathWithinApplication = urlMapping.substring(ServletUtils.extractContextPath(urlMapping).length());
        if (!pathWithinApplication.isEmpty()) {
            requestMatchers.add(mvc.pattern(pathWithinApplication));
        } else {
            requestMatchers.add(mvc.pattern(urlMapping));
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        RequestMatcher simulationEndpointsRequestMatcher = getSimulationEndpointsRequestMatcher(mvc);

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

    private RequestMatcher getSimulationEndpointsRequestMatcher(MvcRequestMatcher.Builder mvc) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();

        if (!Objects.isNull(simulatorRestConfigurationProperties) && !Objects.isNull(simulatorRestAdapter)) {
            requestMatchers.add(mvc.pattern(simulatorRestAdapter.urlMapping(simulatorRestConfigurationProperties)));
        } else if (!Objects.isNull(simulatorRestConfigurationProperties)) {
            requestMatchers.add(mvc.pattern(simulatorRestConfigurationProperties.getUrlMapping()));
        }
        if (!Objects.isNull(simulatorServletRegistrationBean) && simulatorServletRegistrationBean.isEnabled()) {
            simulatorServletRegistrationBean.getUrlMappings().forEach(urlMapping -> addPathWithinApplicationAwareServletMatchers(mvc, urlMapping, requestMatchers));
        }

        if (requestMatchers.isEmpty()) {
            requestMatchers.add(mvc.pattern("*"));
        }

        return new OrRequestMatcher(requestMatchers.toArray(new RequestMatcher[0]));
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
