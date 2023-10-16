package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.TestResult;
import org.citrusframework.simulator.repository.TestResultRepository;
import org.citrusframework.simulator.service.TestResultService;
import org.citrusframework.simulator.service.dto.TestResultByStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link org.citrusframework.simulator.model.TestResult}.
 */
@Service
@Transactional
public class TestResultServiceImpl implements TestResultService {

    private final Logger logger = LoggerFactory.getLogger(TestResultServiceImpl.class);

    private final TestResultRepository testResultRepository;

    public TestResultServiceImpl(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    @Override
    public TestResult transformAndSave(org.citrusframework.TestResult testResult) {
        logger.debug("Request to save citrus TestResult : {}", testResult);
        return save(new TestResult(testResult));
    }

    @Override
    public TestResult save(TestResult testResult) {
        logger.debug("Request to save TestResult : {}", testResult);
        return testResultRepository.save(testResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestResult> findAll(Pageable pageable) {
        logger.debug("Request to get all TestResults");
        return testResultRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestResult> findOne(Long id) {
        logger.debug("Request to get TestResult : {}", id);
        return testResultRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResultByStatus countByStatus() {
        logger.debug("count total by status");
        return testResultRepository.countByStatus();
    }
}
