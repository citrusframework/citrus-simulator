package com.consol.citrus.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.*;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class InboundXmlDataDictionaryTest {

    private TestContext context = Mockito.mock(TestContext.class);

    private String input = String.format("<v1:TestRequest xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"100\" name=\"string\">%n" +
            "  <v1:name>string</v1:name>%n" +
            "  <v1:id>100</v1:id>%n" +
            "  <v1:flag>true</v1:flag>%n" +
            "  <v1:restricted>stringstri</v1:restricted>%n" +
            "</v1:TestRequest>");

    @Test
    public void testInboundDictionary() throws Exception {
        InboundXmlDataDictionary dictionary = new InboundXmlDataDictionary(new SimulatorConfigurationProperties());
        dictionary.afterPropertiesSet();

        when(context.getNamespaceContextBuilder()).thenReturn(new NamespaceContextBuilder());
        when(context.replaceDynamicContentInString(anyString())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Message request = new DefaultMessage(input);
        Message translated = dictionary.interceptMessageConstruction(request, MessageType.XML.name(), context);

        Assert.assertEquals(translated.getPayload(String.class), String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<v1:TestRequest xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"@ignore@\" id=\"@ignore@\" name=\"@ignore@\">%n" +
                "  <v1:name>@ignore@</v1:name>%n" +
                "  <v1:id>@ignore@</v1:id>%n" +
                "  <v1:flag>@ignore@</v1:flag>%n" +
                "  <v1:restricted>@ignore@</v1:restricted>%n" +
                "</v1:TestRequest>%n"));
    }
}