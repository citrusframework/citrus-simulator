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

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.citrusframework.simulator.http.SimulatorRestAdapter;
import org.citrusframework.simulator.http.SimulatorRestConfigurationProperties;
import org.citrusframework.simulator.ws.SimulatorWebServiceAdapter;
import org.citrusframework.simulator.ws.SimulatorWebServiceConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@AllArgsConstructor
public class SimulationMappings {

    private final @Nullable SimulatorRestConfigurationProperties simulatorRestConfigurationProperties;
    private final @Nullable SimulatorRestAdapter simulatorRestAdapter;

    private final @Nullable SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties;
    private final @Nullable SimulatorWebServiceAdapter simulatorWebServiceAdapter;

    public List<String> getServletMappings() {
        List<String> urlMappings = new ArrayList<>();

        if (nonNull(simulatorWebServiceConfigurationProperties)
            && nonNull(simulatorWebServiceAdapter)
            && !isEmpty(simulatorWebServiceAdapter.servletMappings(simulatorWebServiceConfigurationProperties))) {
            urlMappings.addAll(simulatorWebServiceAdapter.servletMappings(simulatorWebServiceConfigurationProperties));
        } else if (nonNull(simulatorWebServiceConfigurationProperties)
            && !isEmpty(simulatorWebServiceConfigurationProperties.getServletMappings())) {
            urlMappings.addAll(simulatorWebServiceConfigurationProperties.getServletMappings());
        }

        return urlMappings;
    }

    public List<String> getUrlMappings() {
        List<String> urlMappings = new ArrayList<>();

        if (nonNull(simulatorRestConfigurationProperties)
            && nonNull(simulatorRestAdapter)) {
            urlMappings.addAll(simulatorRestAdapter.urlMappings(simulatorRestConfigurationProperties));
        } else if (nonNull(simulatorRestConfigurationProperties)
            && !isEmpty(simulatorRestConfigurationProperties.getUrlMappings())) {
            urlMappings.addAll(simulatorRestConfigurationProperties.getUrlMappings());
        }

        return urlMappings;
    }
}
