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

package com.consol.citrus.simulator.sample.file.interceptor;


import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class FileInterceptor extends ChannelInterceptorAdapter {

    private final FileToStringTransformer fileToStringTransformer;

    @Autowired
    public FileInterceptor(@Qualifier("fileToStringTransformer") FileToStringTransformer fileToStringTransformer) {
        this.fileToStringTransformer = fileToStringTransformer;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return ((message != null) && (message.getPayload() != null) && (message.getPayload() instanceof File)) ?
               fileToStringTransformer.transform(message) : message;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return ((message != null) && (message.getPayload() != null) && (message.getPayload() instanceof File)) ?
               fileToStringTransformer.transform(message) : message;
    }
}
