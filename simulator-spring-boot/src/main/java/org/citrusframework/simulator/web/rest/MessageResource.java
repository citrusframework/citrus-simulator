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

package org.citrusframework.simulator.web.rest;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.service.MessageQueryService;
import org.citrusframework.simulator.service.MessageService;
import org.citrusframework.simulator.service.criteria.MessageCriteria;
import org.citrusframework.simulator.web.rest.dto.MessageDTO;
import org.citrusframework.simulator.web.rest.dto.mapper.MessageMapper;
import org.citrusframework.simulator.web.util.PaginationUtil;
import org.citrusframework.simulator.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Message}.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private static final Logger logger = LoggerFactory.getLogger(MessageResource.class);

    private final MessageService messageService;
    private final MessageQueryService messageQueryService;

    private final MessageMapper messageMapper;

    public MessageResource(MessageService messageService, MessageQueryService messageQueryService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageQueryService = messageQueryService;
        this.messageMapper = messageMapper;
    }

    /**
     * {@code GET  /messages} : get all the messages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of messages in body.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getAllMessages(MessageCriteria criteria, @ParameterObject Pageable pageable) {
        logger.debug("REST request to get Messages by criteria: {}", criteria);

        Page<Message> page = messageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent().stream().map(messageMapper::toDto).toList());
    }

    /**
     * {@code GET  /messages/count} : count all the messages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/messages/count")
    public ResponseEntity<Long> countMessages(MessageCriteria criteria) {
        logger.debug("REST request to count Messages by criteria: {}", criteria);
        return ResponseEntity.ok().body(messageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /messages/:id} : get the "id" message.
     *
     * @param id the id of the message to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the message, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable("id") Long id) {
        logger.debug("REST request to get Message : {}", id);
        Optional<Message> message = messageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(message.map(messageMapper::toDto));
    }
}
