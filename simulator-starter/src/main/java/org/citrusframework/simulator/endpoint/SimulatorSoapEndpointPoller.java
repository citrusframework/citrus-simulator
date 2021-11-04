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

package org.citrusframework.simulator.endpoint;

import com.consol.citrus.message.Message;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.simulator.ws.SoapMessageHelper;
import com.consol.citrus.ws.message.SoapMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Christoph Deppisch
 */
public class SimulatorSoapEndpointPoller extends SimulatorEndpointPoller {

    @Autowired
    private SoapMessageHelper soapMessageHelper;

    @Override
    protected Message processRequestMessage(Message request) {
        try {
            return new SoapMessage(soapMessageHelper.getSoapBody(request), request.getHeaders());
        } catch (Exception e) {
            throw new SimulatorException("Unexpected error while processing SOAP request", e);
        }
    }

    @Override
    protected Message processResponseMessage(Message response) {
        return soapMessageHelper.createSoapMessage(response);
    }
}
