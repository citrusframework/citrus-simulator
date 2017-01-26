package com.consol.citrus.simulator.service;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * Service for persisting and retrieving {@link Message} data.
 */
@Service
@Transactional
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    public Message saveMessage(Message.Direction direction, String payload) {
        Message message = new Message();
        message.setDate(new Date());
        message.setDirection(direction);
        message.setPayload(payload);
        return messageRepository.save(message);
    }

    public Message getMessageById(Long id) {
        return messageRepository.findOne(id);
    }

    public List<Message> getMessagesByDateBetween(Date fromDate, Date toDate, Integer page, Integer size) {
        Date calcFromDate = fromDate;
        if (calcFromDate == null) {
            calcFromDate = Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
        }
        Date calcToDate = toDate;
        if (calcToDate == null) {
            calcToDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        Integer calcPage = page;
        if (calcPage == null) {
            calcPage = 0;
        }

        Integer calcSize = size;
        if (calcSize == null) {
            calcSize = 10;
        }

        Pageable pageable = new PageRequest(calcPage, calcSize);
        return messageRepository.findByDateBetweenOrderByDateDesc(calcFromDate, calcToDate, pageable);
    }

    public void clearMessages() {
        messageRepository.deleteAll();
    }

}
