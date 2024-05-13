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

package org.citrusframework.simulator.ui.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ServletUtils {

    private static final String CONTEXT_PATH_PATTERN_STRING = "(.*?)(/\\*\\*?(/\\*)?)?$";
    private static final Pattern CONTEXT_PATH_PATTERN = Pattern.compile(CONTEXT_PATH_PATTERN_STRING);

    private ServletUtils() {
        // Static utility class
    }

    static CharSequence extractContextPath(CharSequence urlMapping) {
        Matcher matcher = CONTEXT_PATH_PATTERN.matcher(urlMapping);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return urlMapping;
    }
}
