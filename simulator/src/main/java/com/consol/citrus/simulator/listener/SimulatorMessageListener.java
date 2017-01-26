package com.consol.citrus.simulator.listener;


import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.report.MessageListener;
import com.consol.citrus.simulator.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This listener is called when the simulator sends or receives messages and is responsible for ensuring
 * the messages are persisted within the simulator database.
 */
@Component
public class SimulatorMessageListener implements MessageListener {

    @Autowired
    MessageService messageService;

    @Override
    public void onInboundMessage(Message message, TestContext context) {
        String payload = message.getPayload().toString();
        messageService.saveMessage(com.consol.citrus.simulator.model.Message.Direction.INBOUND, payload);
    }

    @Override
    public void onOutboundMessage(Message message, TestContext context) {
        String payload = message.getPayload().toString();
        messageService.saveMessage(com.consol.citrus.simulator.model.Message.Direction.OUTBOUND, payload);
    }
}
