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

package org.citrusframework.simulator.web.rest.dto;

import java.time.Instant;
import java.util.Set;

public record TestResultDTO(
    Long id,
    String status,
    String testName,
    String className,
    Set<TestParameterDTO> testParameters,
    String errorMessage,
    String stackTrace,
    String failureType,
    Instant createdDate,
    Instant lastModifiedDate) {

    public TestResultDTO(
        Long id,
        String status,
        String testName,
        String className,
        Set<TestParameterDTO> testParameters,
        String errorMessage,
        String stackTrace,
        String failureType) {
        this(id, status, testName, className, testParameters, errorMessage, stackTrace, failureType, null, null);
    }
}
