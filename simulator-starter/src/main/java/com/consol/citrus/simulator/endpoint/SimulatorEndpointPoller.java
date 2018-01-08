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

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Producer;
import com.consol.citrus.messaging.ReplyProducer;
import com.consol.citrus.simulator.exception.SimulatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * @author Christoph Deppisch
 */
public class SimulatorEndpointPoller implements InitializingBean, Runnable, DisposableBean {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(SimulatorEndpointPoller.class);

    @Autowired
    private TestContextFactory testContextFactory;

    /**
     * Endpoint destination that is constantly polled for new messages.
     */
    private Endpoint inboundEndpoint;

    /**
     * Thread running the server
     */
    private SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    /**
     * Message handler for incoming simulator request messages
     */
    private EndpointAdapter endpointAdapter;

    /**
     * Running flag
     */
    private boolean running = false;

    /**
     * Should automatically start on system load
     */
    private boolean autoStart = true;

    /**
     * Polling delay after uncategorized exception occurred.
     */
    private long exceptionDelay = 10000L;

    @Override
    public void run() {
        LOG.info("Simulator endpoint waiting for requests on endpoint '{}'", inboundEndpoint.getName());

        long delay = 0L;
        while (running) {
            try {
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        LOG.error("Failed to delay after uncategorized exception", e);
                        Thread.currentThread().interrupt();
                    } finally {
                        delay = 0;
                    }
                }

                TestContext context = testContextFactory.getObject();
                Message message = inboundEndpoint.createConsumer().receive(context, inboundEndpoint.getEndpointConfiguration().getTimeout());
                if (message != null) {
                    LOG.debug("Processing inbound message '{}'", message.getId());
                    Message response = endpointAdapter.handleMessage(processRequestMessage(message));

                    if (response != null) {
                        Producer producer = inboundEndpoint.createProducer();
                        if (producer instanceof ReplyProducer) {
                            LOG.debug("Sending response message for inbound message '{}'", message.getId());
                            producer.send(processResponseMessage(response), context);
                        }
                    }
                }
            } catch (ActionTimeoutException e) {
                // ignore timeout and continue listening for request messages.
            } catch (SimulatorException | CitrusRuntimeException e) {
                LOG.error("Failed to process message: {}", e.getMessage());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            } catch (Exception e) {
                delay = exceptionDelay;
                LOG.error("Unexpected error while processing: {}", e.getMessage());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Process response message before sending back to client. This gives subclasses
     * the opportunity to manipulate the response.
     *
     * @param response
     * @return
     */
    protected Message processResponseMessage(Message response) {
        return response;
    }

    /**
     * Process request message before handling. This gives subclasses the opportunity
     * to manipulate the request before execution.
     *
     * @param request
     * @return
     */
    protected Message processRequestMessage(Message request) {
        return request;
    }

    /**
     * Start up runnable in separate thread.
     */
    public void start() {
        running = true;
        taskExecutor.setDaemon(true);
        taskExecutor.execute(this);
    }

    /**
     * Stop runnable execution.
     */
    public void stop() {
        running = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (autoStart) {
            start();
        }
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    /**
     * Sets the target inbound endpoint to read messages from.
     *
     * @param inboundEndpoint
     */
    public void setInboundEndpoint(Endpoint inboundEndpoint) {
        this.inboundEndpoint = inboundEndpoint;
    }

    /**
     * Sets the endpoint adapter to delegate messages to.
     *
     * @param endpointAdapter
     */
    public void setEndpointAdapter(EndpointAdapter endpointAdapter) {
        this.endpointAdapter = endpointAdapter;
    }

    /**
     * Enable/disable auto start.
     *
     * @param autoStart
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Sets the exceptionDelay.
     *
     * @param exceptionDelay
     */
    public void setExceptionDelay(long exceptionDelay) {
        this.exceptionDelay = exceptionDelay;
    }

    /**
     * Gets the exceptionDelay.
     *
     * @return
     */
    public long getExceptionDelay() {
        return exceptionDelay;
    }
}
