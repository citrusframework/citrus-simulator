package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.simulator.util.SoapMessageHelper;
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
