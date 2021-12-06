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

package org.citrusframework.simulator.scenario;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
public class ScenarioBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition.getSource().getClass().getAnnotation(Scenario.class) != null) {
            String scenarioName = definition.getScope().getClass().getAnnotation(Scenario.class).value();
            if (StringUtils.hasLength(scenarioName)) {
                return scenarioName;
            }
        }

        if (definition.getSource().getClass().getAnnotation(Starter.class) != null) {
            String starterName = definition.getScope().getClass().getAnnotation(Starter.class).value();
            if (StringUtils.hasLength(starterName)) {
                return starterName;
            }
        }

        return super.generateBeanName(definition, registry);
    }
}
