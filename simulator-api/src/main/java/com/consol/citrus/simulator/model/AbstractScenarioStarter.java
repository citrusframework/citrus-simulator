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

package com.consol.citrus.simulator.model;

import com.consol.citrus.dsl.design.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractScenarioStarter extends ExecutableTestDesignerComponent implements ScenarioStarter {

    /** This starter's name */
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public List<String> getMessageTemplates() {
        return Collections.emptyList();
    }

    @Override
    public List<ScenarioParameter> getScenarioParameter() {
        return Collections.emptyList();
    }

    /**
     * Sets the beanName property.
     *
     * @param beanName
     */
    @Override
    public void setBeanName(String beanName) {
        this.name = beanName;
    }
}
