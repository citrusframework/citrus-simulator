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
