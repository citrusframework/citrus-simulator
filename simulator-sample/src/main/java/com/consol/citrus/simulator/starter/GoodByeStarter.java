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

package com.consol.citrus.simulator.starter;

import com.consol.citrus.simulator.model.AbstractScenarioStarter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component("GoodByeStarter")
@Scope("prototype")
public class GoodByeStarter extends AbstractScenarioStarter {

    @Override
    protected void configure() {
        echo("GoodBye starter was executed!");
        echo("${payload}");
    }

    @Override
    public String getDisplayName() {
        return "GoodBye";
    }

    @Override
    public List<String> getMessageTemplates() {
        return Arrays.asList(new String[] {"Goodbye"});
    }
}
