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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.info.Info;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfoEndpointConfigurationTest {

    @InjectMocks
    private InfoEndpointConfiguration infoEndpointConfiguration;

    @Mock
    private Environment environmentMock;

    @Test
    void shouldContributeActiveProfilesToInfoBuilder() {
        String[] activeProfiles = {"dev", "local"};
        when(environmentMock.getActiveProfiles()).thenReturn(activeProfiles);

        Info.Builder builder = new Info.Builder();
        infoEndpointConfiguration.contribute(builder);

        Info info = builder.build();
        assertEquals(activeProfiles, info.getDetails().get("activeProfiles"));
    }
}
