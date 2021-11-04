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

package org.citrusframework.simulator.http;

import com.consol.citrus.http.controller.HttpMessageController;
import com.consol.citrus.message.RawMessage;
import com.consol.citrus.report.MessageListeners;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.TypeConversionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interceptor for {@literal <citrus-http:server />} endpoints. Adding this interceptor to a http-endpoint ensures that
 * {@code MessageListeners} are notified when a http message is sent or received.
 */
public class InterceptorHttp implements HandlerInterceptor {

    private final MessageListeners messageListeners;

    public InterceptorHttp(MessageListeners messageListeners) {
        this.messageListeners = messageListeners;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (messageListeners != null) {
            messageListeners.onInboundMessage(new RawMessage(getRequestContent(request)), null);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (messageListeners != null) {
            messageListeners.onOutboundMessage(new RawMessage(getResponseContent(request, response, handler)), null);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String getRequestContent(HttpServletRequest request) throws IOException {
        return FileUtils.readToString(request.getInputStream());
    }

    private String getResponseContent(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HttpMessageController) {
            HttpMessageController handlerController = (HttpMessageController) handler;
            ResponseEntity<?> responseEntity = handlerController.getResponseCache(request);
            if (responseEntity != null) {
                return TypeConversionUtils.convertIfNecessary(responseEntity.getBody(), String.class);
            }
        }
        return "Could not extract Http Response";
    }
}
