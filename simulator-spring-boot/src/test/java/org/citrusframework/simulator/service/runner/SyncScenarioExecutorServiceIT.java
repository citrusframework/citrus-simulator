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

package org.citrusframework.simulator.service.runner;

import org.citrusframework.simulator.IntegrationTest;
import org.citrusframework.simulator.service.ScenarioExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@TestPropertySource(properties={"citrus.simulator.mode=sync"})
class SyncScenarioExecutorServiceIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void isDefaultScenarioExecutorService() {
        assertThat(applicationContext.getBean(ScenarioExecutorService.class))
            .isInstanceOf(DefaultScenarioExecutorService.class)
            .isNotInstanceOf(AsyncScenarioExecutorService.class);
    }
}
