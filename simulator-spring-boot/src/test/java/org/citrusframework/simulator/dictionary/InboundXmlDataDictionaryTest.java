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

package org.citrusframework.simulator.dictionary;

import org.citrusframework.context.TestContext;
import org.citrusframework.message.DefaultMessage;
import org.citrusframework.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.util.SpringBeanTypeConverter;
import org.citrusframework.xml.namespace.NamespaceContextBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class InboundXmlDataDictionaryTest {

    private static final String MESSAGE_INPUT = String.format("<v1:TestRequest xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"false\" id=\"100\" name=\"string\">%n" +
        "  <v1:name>string</v1:name>%n" +
        "  <v1:id>100</v1:id>%n" +
        "  <v1:flag>true</v1:flag>%n" +
        "  <v1:restricted>stringstri</v1:restricted>%n" +
        "</v1:TestRequest>");

    @Mock
    private TestContext testContextMock;

    private InboundXmlDataDictionary fixture;

    @BeforeEach
    void beforEachSetup() {
        fixture = new InboundXmlDataDictionary(new SimulatorConfigurationProperties());
        fixture.initialize();
    }

    @Test
    void testInboundDictionary() {
        doReturn(SpringBeanTypeConverter.INSTANCE).when(testContextMock).getTypeConverter();
        doReturn(new NamespaceContextBuilder()).when(testContextMock).getNamespaceContextBuilder();
        doAnswer(invocation -> invocation.getArguments()[0]).when(testContextMock).replaceDynamicContentInString(anyString());

        Message request = new DefaultMessage(MESSAGE_INPUT);
        Message translated = fixture.transform(request, testContextMock);

        assertEquals(translated.getPayload(String.class), String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<v1:TestRequest xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\" flag=\"@ignore@\" id=\"@ignore@\" name=\"@ignore@\">%n" +
            "    <v1:name>@ignore@</v1:name>%n" +
            "    <v1:id>@ignore@</v1:id>%n" +
            "    <v1:flag>@ignore@</v1:flag>%n" +
            "    <v1:restricted>@ignore@</v1:restricted>%n" +
            "</v1:TestRequest>%n").replace("\r", ""));
    }
}
