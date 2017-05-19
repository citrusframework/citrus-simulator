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

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import org.springframework.jms.connection.SingleConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * @author Martin Maher
 */
public class SimulatorJmsAsyncAdapter implements SimulatorJmsAsyncConfigurer {

    @Override
    public ConnectionFactory connectionFactory() {
        return new SingleConnectionFactory();
    }

    @Override
    public String receiveDestinationName() {
        return System.getProperty(RECEIVE_DESTINATION_NAME_KEY, RECEIVE_DESTINATION_VALUE_DEFAULT);
    }

    @Override
    public String sendDestinationName() {
        return System.getProperty(SEND_DESTINATION_NAME_KEY, SEND_DESTINATION_VALUE_DEFAULT);
    }

    @Override
    public boolean useSoapEnvelope() {
        return false;
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }
}

