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

package org.citrusframework.simulator.ws;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.ws.message.SoapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Christoph Deppisch
 */
public class SoapMessageHelper {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(SoapMessageHelper.class);

    @Autowired
    private SoapMessageFactory soapMessageFactory;

    /**
     * Transformer
     */
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * Method reads SOAP body element from SOAP Envelope and transforms body payload to String.
     *
     * @param request
     * @return
     * @throws javax.xml.soap.SOAPException
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     */
    public String getSoapBody(Message request) throws SOAPException, IOException, TransformerException {
        MessageFactory msgFactory = MessageFactory.newInstance();
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "text/xml; charset=" + Charset.forName(System.getProperty("citrus.file.encoding", "UTF-8")));

        SOAPMessage message = msgFactory.createMessage(mimeHeaders, new ByteArrayInputStream(request.getPayload().toString().getBytes(System.getProperty("citrus.file.encoding", "UTF-8"))));
        SOAPBody soapBody = message.getSOAPBody();

        Document body = soapBody.extractContentAsDocument();

        StringResult result = new StringResult();
        transformerFactory.newTransformer().transform(new DOMSource(body), result);

        return result.toString();
    }

    /**
     * Creates a new SOAP message representation from given payload resource. Constructs a SOAP envelope
     * with empty header and payload as body.
     *
     * @param message
     * @return
     * @throws IOException
     */
    public Message createSoapMessage(Message message) {
        try {
            String payload = message.getPayload().toString();

            LOG.info("Creating SOAP message from payload: " + payload);

            WebServiceMessage soapMessage = soapMessageFactory.createWebServiceMessage();
            transformerFactory.newTransformer().transform(
                    new StringSource(payload), soapMessage.getPayloadResult());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            soapMessage.writeTo(bos);

            return new SoapMessage(new String(bos.toByteArray()), message.getHeaders());
        } catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create SOAP message from payload resource", e);
        }
    }
}
