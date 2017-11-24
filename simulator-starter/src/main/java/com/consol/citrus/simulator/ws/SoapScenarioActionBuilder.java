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
package com.consol.citrus.simulator.ws;

import com.consol.citrus.dsl.builder.*;
import com.consol.citrus.endpoint.Endpoint;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioActionBuilder extends SoapActionBuilder {

    /** Scenario endpoint */
    private final Endpoint endpoint;

    /** Spring application context */
    private ApplicationContext applicationContext;

    public SoapScenarioActionBuilder(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public SoapServerActionBuilder server() {
        return new SoapServerActionBuilder(action, endpoint)
                .withApplicationContext(applicationContext);
    }

    public SoapClientActionBuilder client() {
        return new SoapClientActionBuilder(action, endpoint)
                .withApplicationContext(applicationContext);
    }

    /**
     * Default scenario server receive operation.
     * @return
     * @deprecated use {@link #server()}.receive() instead
     */
    @Deprecated
    public SoapServerRequestActionBuilder receive() {
        return server().receive();
    }

    /**
     * Default scenario server send response operation.
     * @return
     * @deprecated use {@link #server()}.send() instead
     */
    @Deprecated
    public SoapServerResponseActionBuilder send() {
        return server().send();
    }

    /**
     * Sends SOAP fault as scenario server response.
     * @return
     */
    public SoapServerFaultResponseActionBuilder sendFault() {
        return new SoapServerActionBuilder(action, endpoint)
                .withApplicationContext(applicationContext)
                .sendFault();
    }

    @Override
    public SoapScenarioActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapScenarioActionBuilder) super.withApplicationContext(applicationContext);
    }
}
