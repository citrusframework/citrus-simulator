package com.consol.citrus.simulator.dictionary;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.*;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.util.XMLUtils;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class OutboundXmlDataDictionaryTest {

    private TestContext context = Mockito.mock(TestContext.class);

    private String input = String.format("<v1:TestResponse xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"100\" name=\"string\">%n" +
            "  <v1:name>string</v1:name>%n" +
            "  <v1:id>100</v1:id>%n" +
            "  <v1:flag>true</v1:flag>%n" +
            "  <v1:restricted>stringstri</v1:restricted>%n" +
            "</v1:TestResponse>");

    @Test
   public void testInboundDictionary() {
        OutboundXmlDataDictionary dictionary = new OutboundXmlDataDictionary(new SimulatorConfigurationProperties());

        when(context.replaceDynamicContentInString(anyString())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Message request = new DefaultMessage(input);
        Message translated = dictionary.transform(request, context);

        String payload = XMLUtils.prettyPrint(translated.getPayload(String.class));
        String controlPayload = XMLUtils.prettyPrint(String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<v1:TestResponse xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"citrus:randomNumber(3)\" name=\"citrus:randomString(6)\">%n" +
                "  <v1:name>citrus:randomString(6)</v1:name>%n" +
                "  <v1:id>citrus:randomNumber(3)</v1:id>%n" +
                "  <v1:flag>true</v1:flag>%n" +
                "  <v1:restricted>citrus:randomString(10)</v1:restricted>%n" +
                "</v1:TestResponse>%n"));
        
        Assert.assertEquals(payload, controlPayload);
    }

}