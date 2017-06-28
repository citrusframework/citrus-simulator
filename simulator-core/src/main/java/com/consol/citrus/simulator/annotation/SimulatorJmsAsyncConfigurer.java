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

/**
 * Common JMS asynchronous simulator configuration
 */
public interface SimulatorJmsAsyncConfigurer extends SimulatorJmsConfigurer {

    /**
     * The system property key for retrieving the JMS inbound destination name. The simulator receives asynchronous
     * messages from this destination.
     */
    String RECEIVE_DESTINATION_NAME_KEY = "citrus.simulator.jms.async.destination.receive";

    /**
     * The default JMS queue from which inbound messages are received
     */
    String RECEIVE_DESTINATION_VALUE_DEFAULT = "Citrus.Simulator.Async.Inbound";

    /**
     * The system property key for retrieving the JMS outbound destination name. The simulator sends asynchronous
     * messages to this destination.
     */
    String SEND_DESTINATION_NAME_KEY = "citrus.simulator.jms.async.destination.send";

    /**
     * The default JMS queue to which outbound messages are sent
     */
    String SEND_DESTINATION_VALUE_DEFAULT = "Citrus.Simulator.Async.Outbound";

    /**
     * Gets the jms destination to send messages to.
     *
     * @return
     */
    String sendDestinationName();
}
