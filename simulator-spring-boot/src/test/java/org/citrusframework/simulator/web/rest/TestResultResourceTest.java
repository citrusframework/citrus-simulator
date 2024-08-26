package org.citrusframework.simulator.web.rest;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.service.TestResultQueryService;
import org.citrusframework.simulator.service.TestResultService;
import org.citrusframework.simulator.web.rest.dto.mapper.TestResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith({MockitoExtension.class})
class TestResultResourceTest {

    @Mock
    private TestResultService testResultServiceMock;

    @Mock
    private TestResultQueryService testResultQueryServiceMock;

    @Mock
    private TestResultMapper testResultMapperMock;

    private SimulatorConfigurationProperties simulatorConfigurationProperties;

    private TestResultResource fixture;

    @BeforeEach
    void beforeEachSetup() {
        simulatorConfigurationProperties = new SimulatorConfigurationProperties();

        fixture = new TestResultResource(
            simulatorConfigurationProperties,
            testResultServiceMock,
            testResultQueryServiceMock,
            testResultMapperMock);
    }

    @Nested
    class DeleteAllTestResults {

        @Test
        void deletesTestResultsIfEnabled() {
            simulatorConfigurationProperties.getSimulationResults().setResetEnabled(true);

            ResponseEntity<Void> response = fixture.deleteAllTestResults();

            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            verify(testResultServiceMock).deleteAll();
        }

        @Test
        void doesNotDeleteTestResultsIfDisabled() {
            simulatorConfigurationProperties.getSimulationResults().setResetEnabled(false);

            ResponseEntity<Void> response = fixture.deleteAllTestResults();

            assertAll(
                () -> assertThat(response.getStatusCode())
                    .isEqualTo(NOT_IMPLEMENTED),
                () -> assertThat(response.getHeaders()).
                    containsEntry(
                        "message",
                        singletonList(
                            "Resetting TestResults is disabled on this simulator, see property 'citrus.simulator.simulation-results.reset-enabled' for more information!"))
            );

            verifyNoInteractions(testResultServiceMock);
        }
    }
}
