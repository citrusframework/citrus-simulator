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

package org.citrusframework.simulator.ws;

import org.citrusframework.report.MessageListeners;
import org.citrusframework.simulator.listener.SimulatorMessageListener;
import org.citrusframework.ws.interceptor.LoggingClientInterceptor;
import org.citrusframework.ws.interceptor.LoggingEndpointInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds web service logging configuration. This configuration ensures that all messages listeners (in particular the
 * {@link SimulatorMessageListener}) are notified of any inbound or outbound soap messages.
 *
 * @author Martin Maher
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ LoggingEndpointInterceptor.class, LoggingClientInterceptor.class })
public class SimulatorWebServiceLoggingAutoConfiguration {

    @Autowired
    private MessageListeners messageListeners;

    @Bean
    @ConditionalOnMissingBean(name = "simulatorLoggingEndpointInterceptor")
    public LoggingEndpointInterceptor loggingEndpointInterceptor() {
        LoggingEndpointInterceptor loggingEndpointInterceptor = new LoggingEndpointInterceptor();
        loggingEndpointInterceptor.setMessageListener(messageListeners);
        return loggingEndpointInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(name = "simulatorLoggingClientInterceptor")
    public LoggingClientInterceptor loggingClientInterceptor() {
        LoggingClientInterceptor loggingClientInterceptor = new LoggingClientInterceptor();
        loggingClientInterceptor.setMessageListener(messageListeners);
        return loggingClientInterceptor;
    }

}
