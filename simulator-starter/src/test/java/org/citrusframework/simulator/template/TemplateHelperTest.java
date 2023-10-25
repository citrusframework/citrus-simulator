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

class TemplateHelperTest {

    private static final String CONTENT = "Some text containing funny characters üöäß";

    static Stream<Arguments> getFileResource() {
        return Stream.of(
            Arguments.of("", "test", ".xml", false),
            Arguments.of("/", "test", ".xml", false),
            Arguments.of("/template", "test", ".xml", false),
            Arguments.of("/template/xml", "test", ".xml", true),
            Arguments.of("/template/xml/", "test", ".xml", true),
            Arguments.of("/template/xml/", "test", "xml", true),
            Arguments.of("/template/xml/", "aaaa", "xml", false)
        );
    }

    @MethodSource
    @ParameterizedTest
    void getFileResource(String basePath, String fileName, String fileExtension, boolean shouldExist) {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        final Resource fileResource = testling.getFileResource(fileName, fileExtension);

        assertEquals(shouldExist, fileResource.exists());
    }

    static Stream<Arguments> getXmlMessageTemplate() {
        return Stream.of(
            Arguments.of("", "test.xml", null),
            Arguments.of("/", "test.xml", null),
            Arguments.of("/template", "test.xml", null),
            Arguments.of("/template/xml", "test.xml", CONTENT),
            Arguments.of("/template/xml/", "test.xml", CONTENT),
            Arguments.of("/template/xml/", "test", CONTENT),
            Arguments.of("/template/xml/", "aaaa", null)
        );
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

    static Stream<Arguments> getJsonMessageTemplate() {
        return Stream.of(
            Arguments.of("", "test.json", null),
            Arguments.of("/", "test.json", null),
            Arguments.of("/template", "test.json", null),
            Arguments.of("/template/json", "test.json", CONTENT),
            Arguments.of("/template/json/", "test.json", CONTENT),
            Arguments.of("/template/json/", "test", CONTENT),
            Arguments.of("/template/json/", "aaaa", null)
        );
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
