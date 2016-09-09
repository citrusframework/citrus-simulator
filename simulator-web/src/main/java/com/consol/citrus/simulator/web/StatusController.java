/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.simulator.web;

import com.consol.citrus.TestResult;
import com.consol.citrus.report.TestResults;
import com.consol.citrus.simulator.listener.SimulatorStatusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private SimulatorStatusListener statusListener;

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model, @RequestParam(name = "clear", required = false, defaultValue = "false") boolean clear) {
        if (clear) {
            statusListener.clearResults();
        }

        final List<TestResult> results = new ArrayList<>();
        statusListener.getTestResults().doWithResults(new TestResults.ResultCallback() {
            @Override
            public void doWithResult(TestResult result) {
                results.add(0, result);
            }
        });

        model.addAttribute("running", statusListener.getRunningTests().values());
        model.addAttribute("results", results);

        return "status";
    }

}
