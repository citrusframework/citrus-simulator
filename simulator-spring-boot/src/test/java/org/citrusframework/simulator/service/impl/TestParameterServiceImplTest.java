package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.repository.TestParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TestParameterServiceImplTest {

    @Mock
    private TestParameterRepository testParameterRepositoryMock;

    private TestParameterServiceImpl fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new TestParameterServiceImpl(testParameterRepositoryMock);
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

        TestParameter testParameter = new TestParameter();
        doReturn(Optional.of(testParameter)).when(testParameterRepositoryMock).findByCompositeId(testResultId, key);

        Optional<TestParameter> maybeTestParameter = fixture.findOne(testResultId, key);

        assertTrue(maybeTestParameter.isPresent());
        assertEquals(testParameter, maybeTestParameter.get());
    }

    @Test
    void testFindOneWithoutTestResult() {
        Long testResultId = 1L;
        String key = "key";

        doReturn(Optional.empty()).when(testParameterRepositoryMock).findByCompositeId(testResultId, key);

        Optional<TestParameter> maybeTestParameter = fixture.findOne(testResultId, key);

        assertFalse(maybeTestParameter.isPresent());
    }
}
