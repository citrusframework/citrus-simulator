/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.common;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class FeatureFlagNotEnabledException extends Exception {

    private final String flag;

    public FeatureFlagNotEnabledException(String flag) {
        super(format("Feature flag '%s' not enabeld!", flag));

        this.flag = flag;
    }
}
