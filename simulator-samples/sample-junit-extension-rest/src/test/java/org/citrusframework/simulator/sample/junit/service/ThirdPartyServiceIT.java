/*
 * Copyright 2024 the original author or authors.
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

package org.citrusframework.simulator.sample.junit.service;

import org.citrusframework.simulator.junit.TestWithCitrusSimulator;
import org.citrusframework.simulator.sample.junit.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class})
@TestWithCitrusSimulator(
    urlProperties = {"simulator.url"},
    scenarioPackages = {"org.citrusframework.simulator.sample.junit.scenario"}
)
class ThirdPartyServiceIT {

    @Autowired
    private ThirdPartyService fixture;

    @Test
    void testHelloRequest() {
        var prename = "timon";
        var lastname = "borter";

        assertThat(fixture.calculateFullName(prename, lastname))
            .isEqualTo(prename + " " + lastname);
    }
}
