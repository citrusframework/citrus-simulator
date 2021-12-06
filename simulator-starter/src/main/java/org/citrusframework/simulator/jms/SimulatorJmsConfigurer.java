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

package org.citrusframework.simulator.jms;

import org.citrusframework.simulator.config.SimulatorConfigurer;

import javax.jms.ConnectionFactory;

/**
 * Common JMS simulator configuration
 */
public interface SimulatorJmsConfigurer extends SimulatorConfigurer {
    /**
     * Gets the jms connection factory.
     *
     * @return
     */
    ConnectionFactory connectionFactory();

    /**
     * Gets the jms destination to receive messages from.
     * @param simulatorJmsConfiguration
     *
     * @return
     */
    String inboundDestination(SimulatorJmsConfigurationProperties simulatorJmsConfiguration);

    /**
     * Gets the jms destination to send messages to.
     * @param simulatorJmsConfiguration
     *
     * @return
     */
    String replyDestination(SimulatorJmsConfigurationProperties simulatorJmsConfiguration);

    /**
     * Should operate with SOAP envelope. This automatically adds SOAP envelope
     * handling to the inbound and outbound messages.
     * @param simulatorJmsConfiguration
     *
     * @return
     */
    boolean useSoap(SimulatorJmsConfigurationProperties simulatorJmsConfiguration);

    /**
     * En-/Disable synchronous communication.
     * @param simulatorJmsConfiguration
     * @return
     */
    boolean synchronous(SimulatorJmsConfigurationProperties simulatorJmsConfiguration);

    /**
     * Pub-Sub Domain.
     * @param simulatorJmsConfiguration
     * @return
     */
    boolean pubSubDomain(SimulatorJmsConfigurationProperties simulatorJmsConfiguration);
}
