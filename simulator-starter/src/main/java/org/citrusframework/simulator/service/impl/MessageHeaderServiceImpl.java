package org.citrusframework.simulator.service.impl;

import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.repository.MessageHeaderRepository;
import org.citrusframework.simulator.service.MessageHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link MessageHeader}.
 */
@Service
@Transactional
public class MessageHeaderServiceImpl implements MessageHeaderService {

    private final Logger log = LoggerFactory.getLogger(MessageHeaderServiceImpl.class);

    private final MessageHeaderRepository messageHeaderRepository;

    public MessageHeaderServiceImpl(MessageHeaderRepository messageHeaderRepository) {
        this.messageHeaderRepository = messageHeaderRepository;
    }

    @Override
    public MessageHeader save(MessageHeader messageHeader) {
        log.debug("Request to save MessageHeader : {}", messageHeader);
        return messageHeaderRepository.save(messageHeader);
    }

    @Override
    public MessageHeader update(MessageHeader messageHeader) {
        log.debug("Request to update MessageHeader : {}", messageHeader);
        return messageHeaderRepository.save(messageHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageHeader> findAll(Pageable pageable) {
        log.debug("Request to get all MessageHeaders");
        return messageHeaderRepository.findAll(pageable);
    }

    public Page<MessageHeader> findAllWithEagerRelationships(Pageable pageable) {
        return messageHeaderRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessageHeader> findOne(Long id) {
        log.debug("Request to get MessageHeader : {}", id);
        return messageHeaderRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete MessageHeader : {}", id);
        messageHeaderRepository.deleteById(id);
    }
}
