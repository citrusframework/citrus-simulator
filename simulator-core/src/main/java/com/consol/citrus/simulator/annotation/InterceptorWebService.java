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

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.RawMessage;
import com.consol.citrus.report.MessageListeners;
import com.consol.citrus.util.XMLUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerHelper;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Interceptor for {@literal <citrus-ws:server />} endpoints. Adding this interceptor to a webservice-endpoint ensures
 * that {@code MessageListeners} are notified when a soap message is sent or received.
 */
public class InterceptorWebService implements EndpointInterceptor {

    private final MessageListeners messageListeners;

    public InterceptorWebService(MessageListeners messageListeners) {
        this.messageListeners = messageListeners;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
        if (messageContext.getRequest() instanceof SoapMessage) {
            handleSoapMessage((SoapMessage) messageContext.getRequest(), true);
        } else {
            handleWebServiceMessage(messageContext.getRequest(), true);
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
        if (messageContext.hasResponse()) {
            if (messageContext.getResponse() instanceof SoapMessage) {
                handleSoapMessage((SoapMessage) messageContext.getResponse(), false);
            } else {
                handleWebServiceMessage(messageContext.getResponse(), false);
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
        return handleResponse(messageContext, endpoint);
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {
    }

    protected void handleWebServiceMessage(WebServiceMessage message, boolean isInbound) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            message.writeTo(os);
            handleMessage(os.toString(), isInbound);
        } catch (IOException e) {
            throw new CitrusRuntimeException("Error extracting WebServiceMessage", e);
        }
    }

    protected void handleSoapMessage(SoapMessage soapMessage, boolean isInbound) throws TransformerException {
        Transformer transformer = createIndentingTransformer();
        StringWriter writer = new StringWriter();

        transformer.transform(soapMessage.getEnvelope().getSource(), new StreamResult(writer));
        handleMessage(XMLUtils.prettyPrint(writer.toString()), isInbound);
    }

    private void handleMessage(String message, boolean isInbound) {
        if (messageListeners != null) {
            if (isInbound) {
                messageListeners.onInboundMessage(new RawMessage(message), null);
            } else {
                messageListeners.onOutboundMessage(new RawMessage(message), null);
            }
        }
    }

    private Transformer createIndentingTransformer() throws TransformerConfigurationException {
        Transformer transformer = new TransformerHelper().createTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

}
