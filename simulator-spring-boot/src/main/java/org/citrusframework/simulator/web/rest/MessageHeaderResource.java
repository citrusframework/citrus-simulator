/*
 * Copyright 2023-2024 the original author or authors.
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

import org.citrusframework.simulator.model.MessageHeader;
import org.citrusframework.simulator.service.MessageHeaderQueryService;
import org.citrusframework.simulator.service.MessageHeaderService;
import org.citrusframework.simulator.service.criteria.MessageHeaderCriteria;
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
 * REST controller for managing {@link MessageHeader}.
 */
@RestController
@RequestMapping("/api")
public class MessageHeaderResource {

    private static final Logger logger = LoggerFactory.getLogger(MessageHeaderResource.class);

    private final MessageHeaderService messageHeaderService;

    private final MessageHeaderQueryService messageHeaderQueryService;

    public MessageHeaderResource(MessageHeaderService messageHeaderService, MessageHeaderQueryService messageHeaderQueryService) {
        this.messageHeaderService = messageHeaderService;
        this.messageHeaderQueryService = messageHeaderQueryService;
    }

    /**
     * {@code GET  /message-headers} : get all the messageHeaders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of messageHeaders in body.
     */
    @GetMapping("/message-headers")
    public ResponseEntity<List<MessageHeader>> getAllMessageHeaders(MessageHeaderCriteria criteria, @ParameterObject Pageable pageable) {
        logger.debug("REST request to get MessageHeaders by criteria: {}", criteria);

        Page<MessageHeader> page = messageHeaderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /message-headers/count} : count all the messageHeaders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/message-headers/count")
    public ResponseEntity<Long> countMessageHeaders(MessageHeaderCriteria criteria) {
        logger.debug("REST request to count MessageHeaders by criteria: {}", criteria);
        return ResponseEntity.ok().body(messageHeaderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /message-headers/:id} : get the "id" messageHeader.
     *
     * @param id the id of the messageHeader to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the messageHeader, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/message-headers/{id}")
    public ResponseEntity<MessageHeader> getMessageHeader(@PathVariable("id") Long id) {
        logger.debug("REST request to get MessageHeader : {}", id);
        Optional<MessageHeader> messageHeader = messageHeaderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(messageHeader);
    }
}
