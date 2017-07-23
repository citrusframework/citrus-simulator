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

package com.consol.citrus.simulator.mapper;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.message.Message;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Mapper that can be used for examining incoming messages and based on the content of the message returns the name
 * of the scenario that should handle the message.
 * <br/> This {@link MappingKeyExtractor} is similar to the {@link XPathPayloadMappingKeyExtractor} with the
 * added advantage that multiple xpath expressions can be used to determine the scenario that handles the
 * message.
 */
public class ContentBasedXPathScenarioMapper implements MappingKeyExtractor {
    private final List<XPathPayloadMappingKeyExtractor> keyExtractors = new ArrayList<>();
    private final NamespaceContextBuilder namespaceContextBuilder;

    public ContentBasedXPathScenarioMapper() {
        this(new NamespaceContextBuilder());
    }

    public ContentBasedXPathScenarioMapper(NamespaceContextBuilder namespaceContextBuilder) {
        this.namespaceContextBuilder = namespaceContextBuilder;
    }

    public ContentBasedXPathScenarioMapper addNamespaceMapping(String alias, String namespaceIdentifier) {
        namespaceContextBuilder.getNamespaceMappings().put(alias, namespaceIdentifier);
        return this;
    }

    public ContentBasedXPathScenarioMapper addXPathExpression(String xpathExpression) {
        keyExtractors.add(createFromXPathExpression(xpathExpression));
        return this;
    }

    private XPathPayloadMappingKeyExtractor createFromXPathExpression(String xpathExpression) {
        XPathPayloadMappingKeyExtractor mappingKeyExtractor = new XPathPayloadMappingKeyExtractor();
        mappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
        mappingKeyExtractor.setXpathExpression(xpathExpression);
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

    private Optional<String> lookupScenarioName(Message request, XPathPayloadMappingKeyExtractor keyExtractor) {
        return Optional.ofNullable(keyExtractor.getMappingKey(request));
    }
}


