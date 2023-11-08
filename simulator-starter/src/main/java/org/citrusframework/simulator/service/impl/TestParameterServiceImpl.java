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

import org.citrusframework.simulator.model.TestParameter;
import org.citrusframework.simulator.repository.TestParameterRepository;
import org.citrusframework.simulator.service.TestParameterService;
import org.citrusframework.simulator.service.TestResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link org.citrusframework.simulator.model.TestParameter}.
 */
@Service
@Transactional
public class TestParameterServiceImpl implements TestParameterService {

    private static final Logger logger = LoggerFactory.getLogger(TestParameterServiceImpl.class);

    private final TestResultService testResultService;
    private final TestParameterRepository testParameterRepository;

    public TestParameterServiceImpl(TestResultService testResultService, TestParameterRepository testParameterRepository) {
        this.testResultService = testResultService;
        this.testParameterRepository = testParameterRepository;
    }

    @Override
    public TestParameter save(TestParameter testParameter) {
        logger.debug("Request to save TestParameter : {}", testParameter);
        return testParameterRepository.save(testParameter);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestParameter> findAll(Pageable pageable) {
        logger.debug("Request to get all TestParameters");
        return testParameterRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestParameter> findOne(Long testResultId, String key) {
        logger.debug("Request to get TestParameter '{}' of TestResult : {}", key, testResultId);
        return testResultService.findOne(testResultId)
            .flatMap(testResult -> testParameterRepository.findById(new TestParameter.TestParameterId(key, testResult)));
    }
}
