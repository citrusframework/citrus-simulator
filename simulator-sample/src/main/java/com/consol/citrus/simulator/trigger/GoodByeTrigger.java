/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.simulator.trigger;

import com.consol.citrus.simulator.model.AbstractUseCaseTrigger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component("GoodByeTrigger")
@Scope("prototype")
public class GoodByeTrigger  extends AbstractUseCaseTrigger {

    @Override
    protected void configure() {
        echo("GoodBye trigger was executed!");
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
