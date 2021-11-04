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

package org.citrusframework.simulator.controller;

import com.consol.citrus.report.TestResults;
import org.citrusframework.simulator.listener.SimulatorStatusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/summary")
public class SummaryController {

    @Autowired
    private SimulatorStatusListener statusListener;

    /**
     * Get a summary of all tests results
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/results")
    public TestResults getSummaryTestResults() {
        return statusListener.getTestResults();
    }

    /**
     * Get a summary of all tests results
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/results")
    public TestResults clearSummaryTestResults() {
        statusListener.clearResults();
        return new TestResults();
    }

    /**
     * Get count of active scenarios
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/active")
    public Integer getSummaryActive() {
        return statusListener.getCountActiveScenarios();
    }

}

