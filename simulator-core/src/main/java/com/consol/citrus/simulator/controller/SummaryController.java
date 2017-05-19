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

package com.consol.citrus.simulator.controller;

import com.consol.citrus.report.TestResults;
import com.consol.citrus.simulator.listener.SimulatorStatusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class SummaryController {

    @Autowired
    SimulatorStatusListener statusListener;

    /**
     * Get a summary of all tests results
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/summary/results")
    public TestResults getSummaryTestResults() {
        return statusListener.getTestResults();
    }

    /**
     * Get a summary of all tests results
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/summary/results")
    public TestResults clearSummaryTestResults() {
        statusListener.clearResults();
        return new TestResults();
    }

    /**
     * Get count of active tests
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/summary/active")
    public Integer getSummaryActive() {
        return statusListener.getCountActiveScenarios();
    }

}

