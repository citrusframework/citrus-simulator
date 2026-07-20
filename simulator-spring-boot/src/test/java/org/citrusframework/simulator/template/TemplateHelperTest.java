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

package org.citrusframework.simulator.template;

import org.citrusframework.spi.Resource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TemplateHelperTest {

    private static final String CONTENT = "Some text containing funny characters üöäß";

    static Stream<Arguments> getFileResource() {
        return Stream.of(
            arguments("", "test", ".xml", false),
            arguments("/", "test", ".xml", false),
            arguments("/template", "test", ".xml", false),
            arguments("/template/xml", "test", ".xml", true),
            arguments("/template/xml/", "test", ".xml", true),
            arguments("/template/xml/", "test", "xml", true),
            arguments("/template/xml/", "aaaa", "xml", false)
        );
    }

    static Stream<Arguments> getXmlMessageTemplate() {
        return Stream.of(
            arguments("", "test.xml", null),
            arguments("/", "test.xml", null),
            arguments("/template", "test.xml", null),
            arguments("/template/xml", "test.xml", CONTENT),
            arguments("/template/xml/", "test.xml", CONTENT),
            arguments("/template/xml/", "test", CONTENT),
            arguments("/template/xml/", "aaaa", null)
        );
    }

    static Stream<Arguments> getJsonMessageTemplate() {
        return Stream.of(
            arguments("", "test.json", null),
            arguments("/", "test.json", null),
            arguments("/template", "test.json", null),
            arguments("/template/json", "test.json", CONTENT),
            arguments("/template/json/", "test.json", CONTENT),
            arguments("/template/json/", "test", CONTENT),
            arguments("/template/json/", "aaaa", null)
        );
    }

    @MethodSource
    @ParameterizedTest
    void getFileResource(String basePath, String fileName, String fileExtension, boolean shouldExist) {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        final Resource fileResource = testling.getFileResource(fileName, fileExtension);

        assertEquals(shouldExist, fileResource.exists());
    }

    @MethodSource
    @ParameterizedTest
    void getXmlMessageTemplate(String basePath, String fileName, String expectedContent) {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        try {
            final String content = testling.getXmlMessageTemplate(fileName);
            assertTrue(content.contains(expectedContent));
        } catch (Exception e) {
            assertNull(expectedContent);
        }
    }

    @MethodSource
    @ParameterizedTest
    void getJsonMessageTemplate(String basePath, String fileName, String expectedContent) {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        try {
            final String content = testling.getJsonMessageTemplate(fileName);
            assertTrue(content.contains(expectedContent));
        } catch (Exception e) {
            assertNull(expectedContent);
        }
    }
}
