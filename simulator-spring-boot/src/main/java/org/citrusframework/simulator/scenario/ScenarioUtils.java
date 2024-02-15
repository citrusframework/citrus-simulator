/*
 * Copyright 2024 the original author or authors.
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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

@NoArgsConstructor(access = PRIVATE)
public class ScenarioUtils {

    /**
     * Retrieves the specified annotation from the class hierarchy of the given scenario object.
     * If the scenario object is a proxy, this method unwraps the proxy to obtain the actual target class.
     *
     * @param scenario       The scenario object to search for the annotation. Must not be null.
     * @param annotationType The type of annotation to retrieve.
     * @param <T>            The type of the annotation.
     * @return The annotation if found, otherwise {@code null}.
     */
    @Nullable
    public static <T extends Annotation> T getAnnotationFromClassHierarchy(@Nonnull SimulatorScenario scenario, Class<T> annotationType) {
        return getAnnotationFromClassHierarchy(ultimateTargetClass(scenario), annotationType);
    }

    /**
     * Retrieves the specified annotation from the class hierarchy of the given scenario class.
     *
     * @param scenarioClass  The class to search for the annotation.
     * @param annotationType The type of annotation to retrieve.
     * @param <T>            The type of the annotation.
     * @return The annotation if found, otherwise {@code null}.
     */
    @Nullable
    public static <T extends Annotation> T getAnnotationFromClassHierarchy(@Nonnull Class<?> scenarioClass, Class<T> annotationType) {
        T annotation = scenarioClass.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        } else if (scenarioClass.getSuperclass() != null) {
            return getAnnotationFromClassHierarchy(scenarioClass.getSuperclass(), annotationType);
        }

        return null;
    }
}
