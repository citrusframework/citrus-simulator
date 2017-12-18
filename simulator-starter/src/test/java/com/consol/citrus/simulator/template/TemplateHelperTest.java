package com.consol.citrus.simulator.template;

import org.junit.Assert;
import org.springframework.core.io.Resource;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TemplateHelperTest {
    private static final String CONTENT = "Some text containing funny characters üöäß";

    @DataProvider
    private Object[][] fileResourceDP() {
        return new Object[][]{
                {"", "test", ".xml", false},
                {"/", "test", ".xml", false},
                {"/template", "test", ".xml", false},
                {"/template/xml", "test", ".xml", true},
                {"/template/xml/", "test", ".xml", true},
                {"/template/xml/", "test", "xml", true},
                {"/template/xml/", "aaaa", "xml", false}
        };
    }

    @Test(dataProvider = "fileResourceDP")
    public void testGetFileResource(String basePath, String fileName, String fileExtension, boolean shouldExist) throws Exception {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        final Resource fileResource = testling.getFileResource(fileName, fileExtension);
        Assert.assertEquals(shouldExist, fileResource.exists());
    }

    @DataProvider
    private Object[][] xmlTemplateDP() {
        return new Object[][]{
                {"", "test.xml", null},
                {"/", "test.xml", null},
                {"/template", "test.xml", null},
                {"/template/xml", "test.xml", CONTENT},
                {"/template/xml/", "test.xml", CONTENT},
                {"/template/xml/", "test", CONTENT},
                {"/template/xml/", "aaaa", null}
        };
    }

    @Test(dataProvider = "xmlTemplateDP")
    public void testGetXmlMessageTemplate(String basePath, String fileName, String expectedContent) throws Exception {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        try {
            final String content = testling.getXmlMessageTemplate(fileName);
            Assert.assertTrue(content.contains(expectedContent));
        } catch (Exception e) {
            Assert.assertNull(expectedContent);
        }
    }

    @DataProvider
    private Object[][] jsonTemplateDP() {
        return new Object[][]{
                {"", "test.json", null},
                {"/", "test.json", null},
                {"/template", "test.json", null},
                {"/template/json", "test.json", CONTENT},
                {"/template/json/", "test.json", CONTENT},
                {"/template/json/", "test", CONTENT},
                {"/template/json/", "aaaa", null}
        };
    }

    @Test(dataProvider = "jsonTemplateDP")
    public void testGetJsonMessageTemplate(String basePath, String fileName, String expectedContent) throws Exception {
        final TemplateHelper testling = TemplateHelper.instance(basePath, UTF_8);
        try {
            final String content = testling.getJsonMessageTemplate(fileName);
            Assert.assertTrue(content.contains(expectedContent));
        } catch (Exception e) {
            Assert.assertNull(expectedContent);
        }
    }
}