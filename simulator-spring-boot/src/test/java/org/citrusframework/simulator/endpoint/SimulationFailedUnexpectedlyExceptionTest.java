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

package org.citrusframework.simulator.endpoint;

import org.citrusframework.simulator.exception.SimulatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.endpoint.SimulationFailedUnexpectedlyException.EXCEPTION_TYPE;

class SimulationFailedUnexpectedlyExceptionTest {

    private static final Throwable TEST_THROWABLE = new SimulatorException("Huston, we hav a problem!");

    private SimulationFailedUnexpectedlyException fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SimulationFailedUnexpectedlyException(TEST_THROWABLE);
    }

    @Test
    void typeIsStatic() {
        assertThat(fixture)
            .extracting(SimulationFailedUnexpectedlyException::getType)
            .isEqualTo(EXCEPTION_TYPE);
    }
}
