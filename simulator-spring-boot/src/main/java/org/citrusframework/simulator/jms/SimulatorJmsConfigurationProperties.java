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

package org.citrusframework.simulator.jms;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christoph Deppisch
 */
@ConfigurationProperties(prefix = "citrus.simulator.jms")
public class SimulatorJmsConfigurationProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorJmsConfigurationProperties.class);

    /**
     * Global option to enable/disable JMS support, default is false.
     */
    private boolean enabled = false;

    /**
     * The JMS inbound destination name. The simulator receives asynchronous messages using this destination.
     */
    private String inboundDestination = "Citrus.Simulator.Inbound";

    /**
     * The JMS reply destination name. The simulator sends asynchronous messages to this destination.
     */
    private String replyDestination = "";

    /**
     * En-/Disable JMS synchronous communication. By default, this option is disabled.
     */
    private boolean synchronous = false;

    /**
     * En-/Disable JMS synchronous communication. By default, this option is disabled.
     */
    private boolean useSoap = false;

    /**
     * Pub-Sum Domain. By default, this option is disabled.
     */
    private boolean pubSubDomain = false;

    /**
     * Gets the enabled.
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the inboundDestination.
     *
     * @return
     */
    public String getInboundDestination() {
        return inboundDestination;
    }

    /**
     * Sets the inboundDestination.
     *
     * @param inboundDestination
     */
    public void setInboundDestination(String inboundDestination) {
        this.inboundDestination = inboundDestination;
    }

    /**
     * Gets the replyDestination.
     *
     * @return
     */
    public String getReplyDestination() {
        return replyDestination;
    }

    /**
     * Sets the replyDestination.
     *
     * @param replyDestination
     */
    public void setReplyDestination(String replyDestination) {
        this.replyDestination = replyDestination;
    }

    /**
     * Gets the synchronous.
     *
     * @return
     */
    public boolean isSynchronous() {
        return synchronous;
    }

    /**
     * Sets the synchronous.
     *
     * @param synchronous
     */
    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    /**
     * Gets the useSoap.
     *
     * @return
     */
    public boolean isUseSoap() {
        return useSoap;
    }

    /**
     * Sets the useSoap.
     *
     * @param useSoap
     */
    public void setUseSoap(boolean useSoap) {
        this.useSoap = useSoap;
    }

    /**
     * Gets the pubsub.
     *
     * @return
     */
    public boolean isPubSubDomain() {
        return pubSubDomain;
    }

    /**
     * Sets the pubsub.
     *
     * @return
     */
    public void setPubSubDomain(boolean pubSubDomain) {
        this.pubSubDomain = pubSubDomain;
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("Using the simulator configuration: {}", this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append(enabled)
            .append(inboundDestination)
            .append(replyDestination)
            .append(synchronous)
            .append(useSoap)
            .append(pubSubDomain)
            .toString();
    }
}
