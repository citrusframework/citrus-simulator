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

package org.citrusframework.simulator.sample.jms.async.scenario;

import jakarta.xml.bind.JAXBElement;
import org.citrusframework.simulator.sample.jms.async.model.CancelFaxType;
import org.citrusframework.simulator.sample.jms.async.model.FaxStatusEnumType;
import org.citrusframework.simulator.sample.jms.async.model.FaxStatusType;
import org.citrusframework.simulator.sample.jms.async.model.FaxType;
import org.citrusframework.simulator.sample.jms.async.model.ObjectFactory;
import org.citrusframework.simulator.sample.jms.async.model.SendFaxType;
import org.citrusframework.xml.Jaxb2Marshaller;
import org.citrusframework.xml.Marshaller;

/**
 * @author Martin Maher
 */
public class PayloadHelper {
    public Marshaller getMarshaller() {
        return new Jaxb2Marshaller(ObjectFactory.class.getPackage().getName());
    }

    public JAXBElement<SendFaxType> generateSendFaxMessage(String clientId, FaxType fax, String referenceId, boolean statusUpdates) {
        return new ObjectFactory().createSendFaxMessage(createSendFaxType(clientId, fax, referenceId, statusUpdates));
    }

    public JAXBElement<CancelFaxType> generateCancelFaxMessage(String referenceId) {
        return new ObjectFactory().createCancelFaxMessage(createCancelFaxType(referenceId));
    }

    public JAXBElement<FaxStatusType> generateFaxStatusMessage(String referenceId, FaxStatusEnumType status, String statusMessage) {
        return new ObjectFactory().createStatusUpdateMessage(createFaxStatusType(referenceId, status, statusMessage));
    }

    public FaxType createFaxType(String contact, String content, String recepient, String sender) {
        FaxType sendFax = new FaxType();
        sendFax.setContact(contact);
        sendFax.setContent(content.getBytes());
        sendFax.setContentType("text");
        sendFax.setRecepientFaxNumber(recepient);
        sendFax.setSenderFaxNumber(sender);
        return sendFax;
    }

    public SendFaxType createSendFaxType(String clientId, FaxType fax, String referenceId, boolean statusUpdates) {
        SendFaxType sendFax = new SendFaxType();
        sendFax.setClientId(clientId);
        sendFax.setFax(fax);
        sendFax.setReferenceId(referenceId);
        sendFax.setSendStatusUpdates(statusUpdates);
        return sendFax;
    }

    public CancelFaxType createCancelFaxType(String referenceId) {
        CancelFaxType cancelFax = new CancelFaxType();
        cancelFax.setReferenceId(referenceId);
        return cancelFax;
    }

    public FaxStatusType createFaxStatusType(String referenceId, FaxStatusEnumType status, String statusMessage) {
        FaxStatusType faxStatus = new FaxStatusType();
        faxStatus.setReferenceId(referenceId);
        faxStatus.setStatus(status);
        faxStatus.setStatusMessage(statusMessage);
        return faxStatus;
    }
}
