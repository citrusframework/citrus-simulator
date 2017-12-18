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

package com.consol.citrus.simulator.sample.util;

import com.consol.citrus.simulator.exception.SimulatorException;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;

public final class JsonMarshaller {
    private JsonMarshaller() {
        // utility class with static methods
    }

    public static String toJson(Object object) {
        final Jackson2JsonObjectMapper mapper = new Jackson2JsonObjectMapper();
        try {
            return mapper.toJson(object);
        } catch (Exception e) {
            throw new SimulatorException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        final Jackson2JsonObjectMapper mapper = new Jackson2JsonObjectMapper();
        try {
            return mapper.fromJson(json, clazz);
        } catch (Exception e) {
            throw new SimulatorException(e);
        }
    }
}
