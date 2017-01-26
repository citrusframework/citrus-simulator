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

