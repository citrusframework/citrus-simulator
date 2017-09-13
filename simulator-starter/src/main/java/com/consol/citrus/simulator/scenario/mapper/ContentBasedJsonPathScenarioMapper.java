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

package com.consol.citrus.simulator.scenario.mapper;

import com.consol.citrus.endpoint.adapter.mapping.JsonPayloadMappingKeyExtractor;
import com.consol.citrus.message.Message;

import java.util.*;

/**
 * Mapper that can be used for examining incoming messages and based on the content of the message returns the name
 * of the scenario that should handle the message.
 * <br/> This {@link ScenarioMapper} is similar to the {@link JsonPayloadMappingKeyExtractor} with the
 * added advantage that multiple jsonPath expressions can be used to determine the scenario that handles the
 * message.
 */
public class ContentBasedJsonPathScenarioMapper implements ScenarioMapper {

    private final List<JsonPayloadMappingKeyExtractor> keyExtractors = new ArrayList<>();

    /**
     * Fluent API builder method adds new json path expression.
     * @param jsonPathExpression
     * @return
     */
    public ContentBasedJsonPathScenarioMapper addJsonPathExpression(String jsonPathExpression) {
        keyExtractors.add(createFromXPathExpression(jsonPathExpression));
        return this;
    }

    /**
     * Create proper json path mapping key extractor from given expression.
     * @param jsonPathExpression
     * @return
     */
    private JsonPayloadMappingKeyExtractor createFromXPathExpression(String jsonPathExpression) {
        JsonPayloadMappingKeyExtractor mappingKeyExtractor = new JsonPayloadMappingKeyExtractor();
        mappingKeyExtractor.setJsonPathExpression(jsonPathExpression);
        return mappingKeyExtractor;
    }

    @Override
    public String extractMappingKey(Message request) {
        Optional<String> v = keyExtractors.parallelStream()
                .map(keyExtractor -> lookupScenarioName(request, keyExtractor))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
        return v.orElse(null);
    }

    /**
     * Look up scenario name for given request.
     * @param request
     * @param keyExtractor
     * @return
     */
    private Optional<String> lookupScenarioName(Message request, JsonPayloadMappingKeyExtractor keyExtractor) {
        return Optional.ofNullable(keyExtractor.getMappingKey(request));
    }
}


