package org.citrusframework.simulator.dictionary;

import org.citrusframework.context.TestContext;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.util.XMLUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class OutboundXmlDataDictionaryTest {

    private static final String MESSAGE_INPUT = String.format("<v1:TestResponse xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"100\" name=\"string\">%n" +
        "  <v1:name>string</v1:name>%n" +
        "  <v1:id>100</v1:id>%n" +
        "  <v1:flag>true</v1:flag>%n" +
        "  <v1:restricted>stringstri</v1:restricted>%n" +
        "</v1:TestResponse>");

    @Mock
    private TestContext testContextMock;

    private OutboundXmlDataDictionary fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new OutboundXmlDataDictionary(new SimulatorConfigurationProperties());
    }

    @Test
    void testInboundDictionary() {
        when(testContextMock.replaceDynamicContentInString(anyString())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Message request = new DefaultMessage(MESSAGE_INPUT);
        Message translated = fixture.transform(request, testContextMock);

        String payload = XMLUtils.prettyPrint(translated.getPayload(String.class));
        String controlPayload = XMLUtils.prettyPrint(String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<v1:TestResponse xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"citrus:randomNumber(3)\" name=\"citrus:randomString(6)\">%n" +
            "  <v1:name>citrus:randomString(6)</v1:name>%n" +
            "  <v1:id>citrus:randomNumber(3)</v1:id>%n" +
            "  <v1:flag>true</v1:flag>%n" +
            "  <v1:restricted>citrus:randomString(10)</v1:restricted>%n" +
            "</v1:TestResponse>%n"));

        assertEquals(payload, controlPayload);
    }
}
