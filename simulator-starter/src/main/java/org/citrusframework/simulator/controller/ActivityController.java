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

import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecutionFilter;
import org.citrusframework.simulator.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Date;

@RestController
@RequestMapping("api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @RequestMapping(method = RequestMethod.GET)
    public Collection<ScenarioExecution> getScenarioExecutions(
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        return activityService.getScenarioExecutionsByStartDate(fromDate, toDate, page, size);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void clearExecutions() {
        activityService.clearScenarioExecutions();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/scenario/{name}")
    public Collection<ScenarioExecution> getScenarioExecutionsByName(@PathVariable("name") String name) {
        return activityService.getScenarioExecutionsByName(name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status/{status}")
    public Collection<ScenarioExecution> getScenarioExecutionsByStatus(@PathVariable("status") String status) {
        return activityService.getScenarioExecutionsByStatus(ScenarioExecution.Status.valueOf(status));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ScenarioExecution getScenarioExecution(@PathVariable("id") Long id) {
        return activityService.getScenarioExecutionById(id);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public Collection<ScenarioExecution> getScenarioExecutions(@RequestBody ScenarioExecutionFilter filter) {
        return activityService.getScenarioExecutions(filter);
    }

}
