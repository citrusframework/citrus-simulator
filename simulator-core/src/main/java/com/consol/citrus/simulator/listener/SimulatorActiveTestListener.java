/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.listener;

import com.consol.citrus.TestCase;
import com.consol.citrus.report.AbstractTestListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Christoph Deppisch
 */
@Component
public class SimulatorActiveTestListener extends AbstractTestListener {

    /**
     * List of active tests
     */
    private ConcurrentLinkedQueue<TestCase> activeTests = new ConcurrentLinkedQueue<>();

    /**
     * Maximum capacity of active tests
     */
    private int queueCapacity = 1000;

    @Override
    public void onTestStart(TestCase testCase) {
        if (!activeTests.contains(testCase)) {
            activeTests.add(testCase);
        }

        while (activeTests.size() > queueCapacity) {
            activeTests.remove();
        }
    }

    @Override
    public void onTestFinish(TestCase testCase) {
        activeTests.remove(testCase);
    }

    /**
     * Gets the value of the activeTests property.
     *
     * @return the activeTests
     */
    public TestCase[] getActiveTests() {
        return activeTests.toArray(new TestCase[activeTests.size()]);
    }
}
