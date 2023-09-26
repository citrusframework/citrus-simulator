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
