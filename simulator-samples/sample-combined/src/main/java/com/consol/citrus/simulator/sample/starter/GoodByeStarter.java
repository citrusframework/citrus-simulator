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

package com.consol.citrus.simulator.sample.starter;

import com.consol.citrus.simulator.model.ScenarioParameter;
import com.consol.citrus.simulator.model.ScenarioParameterBuilder;
import com.consol.citrus.simulator.scenario.*;
import com.consol.citrus.simulator.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Starter("GoodByeStarter")
public class GoodByeStarter extends AbstractScenarioStarter {

    @Autowired
    private TemplateService templateService;

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.echo("GoodBye starter was executed!");
        scenario.echo("${payload}");
    }
    @Override
    public Collection<ScenarioParameter> getScenarioParameters() {
        List<ScenarioParameter> scenarioParameters = new ArrayList<>();

        // greeting (text area)
        scenarioParameters.add(new ScenarioParameterBuilder()
                .name("payload")
                .label("Payload")
                .required()
                .textarea()
                .value(templateService.getXmlMessageTemplate("Goodbye"))
                .build());

        return scenarioParameters;
    }

}
