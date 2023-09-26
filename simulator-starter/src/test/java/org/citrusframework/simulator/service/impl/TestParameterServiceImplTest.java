package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.TestParameterRepository;
import org.citrusframework.simulator.service.TestResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TestParameterServiceImplTest {

    @Mock
    private TestResultService testResultServiceMock;

    @Mock
    private TestParameterRepository testParameterRepositoryMock;

    private TestParameterServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new TestParameterServiceImpl(testResultServiceMock, testParameterRepositoryMock);
    }

    @Test
    void testSave() {
        TestParameter testParameter = new TestParameter();
        doReturn(testParameter).when(testParameterRepositoryMock).save(testParameter);

        TestParameter result = fixture.save(testParameter);

        assertEquals(testParameter, result);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<TestParameter> page = mock(Page.class);
        doReturn(page).when(testParameterRepositoryMock).findAll(pageable);

        Page<TestParameter> result = fixture.findAll(pageable);

        assertEquals(page, result);
    }

    @Test
    void testFindOneWithExistingTestResult() {
        Long testResultId = 1L;
        String key = "key";

        TestResult testResult = new TestResult().id(testResultId);
        Optional<TestResult> optionalTestResult = Optional.of(testResult);
        doReturn(optionalTestResult).when(testResultServiceMock).findOne(testResultId);

        TestParameter testParameter = new TestParameter();
        doReturn(Optional.of(testParameter)).when(testParameterRepositoryMock).findById(any(TestParameter.TestParameterId.class));

        Optional<TestParameter> maybeTestParameter = fixture.findOne(testResultId, key);

        assertTrue(maybeTestParameter.isPresent());
        assertEquals(testParameter, maybeTestParameter.get());

        ArgumentCaptor<TestParameter.TestParameterId> testParameterIdArgumentCaptor = ArgumentCaptor.forClass(TestParameter.TestParameterId.class);
        verify(testParameterRepositoryMock).findById(testParameterIdArgumentCaptor.capture());

        TestParameter.TestParameterId testParameterId = testParameterIdArgumentCaptor.getValue();
        assertEquals(testResultId, testParameterId.testResultId);
        assertEquals(key, testParameterId.key);
    }

    @Test
    void testFindOneWithoutTestResult() {
        Long testResultId = 1L;
        String key = "key";

        doReturn(Optional.empty()).when(testResultServiceMock).findOne(testResultId);

        Optional<TestParameter> maybeTestParameter = fixture.findOne(testResultId, key);

        assertFalse(maybeTestParameter.isPresent());

        verifyNoInteractions(testParameterRepositoryMock);
    }
}
