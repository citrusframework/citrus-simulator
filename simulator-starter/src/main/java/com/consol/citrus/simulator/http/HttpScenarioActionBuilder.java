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
package com.consol.citrus.simulator.http;

import com.consol.citrus.dsl.builder.HttpActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.builder.HttpServerActionBuilder;
import com.consol.citrus.endpoint.Endpoint;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class HttpScenarioActionBuilder extends HttpActionBuilder {

    private final Endpoint endpoint;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public HttpScenarioActionBuilder(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * ActionBuilder for sending HTTP requests and receiving HTTP responses
     * @return the HTTP client action builder
     */
    public HttpClientActionBuilder client() {
        return new HttpClientActionBuilder(action, endpoint)
                .withApplicationContext(applicationContext);
    }

    /**
     * ActionBuilder for receiving HTTP requests and sending HTTP responses
     * @return the HTTP server action builder
     */
    public HttpServerActionBuilder server() {
        return new HttpServerActionBuilder(action, endpoint)
                .withApplicationContext(applicationContext);
    }

    /**
     * Action builder for receiving a HTTP Server request
     * @return
     * @deprecated use {@link #server()}.receive() instead
     */
    @Deprecated
    public HttpServerActionBuilder.HttpServerReceiveActionBuilder receive() {
        return server().receive();
    }

    /**
     * Action builder for sending a HTTP Server response
     * @return
     * @deprecated use {@link #server()}.send() instead
     */
    @Deprecated
    public HttpServerActionBuilder.HttpServerSendActionBuilder send() {
        return server().send();
    }

    @Override
    public HttpScenarioActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (HttpScenarioActionBuilder) super.withApplicationContext(applicationContext);
    }
}
