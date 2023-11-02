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

package org.citrusframework.simulator.service.dto;

import java.util.Objects;

public record TestResultByStatus(Long successful, Long failed, Long total) {

    public TestResultByStatus(Long successful, Long failed) {
        this(Objects.isNull(successful) ? 0 : successful, Objects.isNull(failed) ? 0 : failed, Objects.isNull(successful) || Objects.isNull(failed) ? 0 : successful + failed);
    }
}
