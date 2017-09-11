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

import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.message.Message;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;

import java.util.*;

/**
 * Mapper that can be used for examining incoming messages and based on the content of the message returns the name
 * of the scenario that should handle the message.
 * <br/> This {@link ScenarioMapper} is similar to the {@link XPathPayloadMappingKeyExtractor} with the
 * added advantage that multiple xpath expressions can be used to determine the scenario that handles the
 * message.
 */
public class ContentBasedXPathScenarioMapper implements ScenarioMapper {

    private final List<XPathPayloadMappingKeyExtractor> keyExtractors = new ArrayList<>();
    private final NamespaceContextBuilder namespaceContextBuilder;

    /**
     * Default constructor.
     */
    public ContentBasedXPathScenarioMapper() {
        this(new NamespaceContextBuilder());
    }

    /**
     * Default constructor using namespace context builder.
     * @param namespaceContextBuilder
     */
    public ContentBasedXPathScenarioMapper(NamespaceContextBuilder namespaceContextBuilder) {
        this.namespaceContextBuilder = namespaceContextBuilder;
    }

    /**
     * Fluent API builder method adds new namespace mapping.
     * @param alias
     * @param namespaceIdentifier
     * @return
     */
    public ContentBasedXPathScenarioMapper addNamespaceMapping(String alias, String namespaceIdentifier) {
        namespaceContextBuilder.getNamespaceMappings().put(alias, namespaceIdentifier);
        return this;
    }

    /**
     * Fluent API builder method adds new xpath expression.
     * @param xpathExpression
     * @return
     */
    public ContentBasedXPathScenarioMapper addXPathExpression(String xpathExpression) {
        keyExtractors.add(createFromXPathExpression(xpathExpression));
        return this;
    }

    /**
     * Create proper xpath mapping key extractor from given expression.
     * @param xpathExpression
     * @return
     */
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

    /**
     * Lookup scenario name from given request.
     * @param request
     * @param keyExtractor
     * @return
     */
    private Optional<String> lookupScenarioName(Message request, XPathPayloadMappingKeyExtractor keyExtractor) {
        return Optional.ofNullable(keyExtractor.getMappingKey(request));
    }
}


