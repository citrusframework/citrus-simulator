package com.consol.citrus.simulator.controller;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    MessageService messageService;

    @RequestMapping(method = RequestMethod.GET, value = "/message")
    public Collection<Message> getMessages(
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        return messageService.getMessagesByDateBetween(fromDate, toDate, page, size);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/message")
    public void clearMessages() {
        messageService.clearMessages();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/message/{id}")
    public Message getMessageById(@PathVariable("id") Long id) {
        return messageService.getMessageById(id);
    }

}
