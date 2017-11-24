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

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.builder.HttpActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerFaultResponseActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerRequestActionBuilder;
import com.consol.citrus.dsl.builder.SoapServerResponseActionBuilder;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.simulator.scenario.ScenarioRunner;
import org.springframework.context.ApplicationContext;

/**
 * @author Christoph Deppisch
 */
public class SoapScenarioRunnerActionBuilder extends HttpActionBuilder {

    private final Endpoint endpoint;
    private final ScenarioRunner runner;
    private ApplicationContext applicationContext;

    public SoapScenarioRunnerActionBuilder(ScenarioRunner runner, Endpoint endpoint) {
        this.runner = runner;
        this.endpoint = endpoint;
    }

    /**
     * soap server action builder for receiving soap requests and sending responses
     *
     * @return the SOAP Server action builder
     */
    public SoapServerScenarioRunnerActionBuilder server() {
        return new SoapServerScenarioRunnerActionBuilder(runner, endpoint)
                .withApplicationContext(applicationContext);
    }

    /**
     * soap client action builder for sending soap requests and receiving responses
     *
     * @return the SOAP Client action builder
     */
    public SoapClientScenarioRunnerActionBuilder client() {
        return new SoapClientScenarioRunnerActionBuilder(runner, endpoint)
                .withApplicationContext(applicationContext);
    }

    /**
     * Default scenario receive operation.
     *
     * @return
     * @deprecated use {@link #server()}.receive() instead
     */
    @Deprecated
    public TestAction receive(SoapBuilderSupport<SoapServerRequestActionBuilder> configurer) {
        return server().receive(configurer);
    }

    /**
     * Default scenario send response operation.
     *
     * @return
     * @deprecated use {@link #server()}.send() instead
     */
    @Deprecated
    public TestAction send(SoapBuilderSupport<SoapServerResponseActionBuilder> configurer) {
        return server().send(configurer);
    }

    /**
     * Default scenario send fault operation.
     *
     * @return
     * @deprecated use {@link #server()}.sendFault() instead
     */
    @Deprecated
    public TestAction sendFault(SoapBuilderSupport<SoapServerFaultResponseActionBuilder> configurer) {
        return server().sendFault(configurer);
    }

    @Override
    public SoapScenarioRunnerActionBuilder withApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return (SoapScenarioRunnerActionBuilder) super.withApplicationContext(applicationContext);
    }
}
