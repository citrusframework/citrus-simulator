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

package org.citrusframework.simulator.controller;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;
import org.citrusframework.simulator.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public Collection<Message> getMessages(@RequestBody MessageFilter filter) {
        return messageService.getMessagesMatchingFilter(filter);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void clearMessages() {
        messageService.clearMessages();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Message getMessageById(@PathVariable("id") Long id) {
        return messageService.getMessageById(id);
    }

}
