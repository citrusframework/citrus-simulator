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

package org.citrusframework.simulator.endpoint;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.context.TestContextFactory;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Producer;
import com.consol.citrus.messaging.ReplyProducer;
import org.citrusframework.simulator.exception.SimulatorException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.*;

/**
 * @author Christoph Deppisch
 */
public class SimulatorEndpointPoller implements InitializingBean, Runnable, DisposableBean, ApplicationListener<ContextClosedEvent> {

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

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("endpoint-poller-thread-%d")
            .build();

    /**
     * Thread running the server
     */
    private ExecutorService taskExecutor = Executors.newSingleThreadExecutor(threadFactory);

    /**
     * Message handler for incoming simulator request messages
     */
    private EndpointAdapter endpointAdapter;

    /**
     * Running flag
     */
    private CompletableFuture<Boolean> running = new CompletableFuture<>();

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
        while (running.getNow(true)) {
            try {
                if (delay > 0) {
                    try {
                        if (!running.get(delay, TimeUnit.MILLISECONDS)) {
                            continue;
                        }
                    } catch (TimeoutException e) {
                        LOG.info("Continue simulator endpoint polling after uncategorized exception");
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
        taskExecutor.execute(this);
    }

    /**
     * Stop runnable execution.
     */
    public void stop() {
        LOG.info("Simulator endpoint poller terminating ...");

        running.complete(false);

        try {
            taskExecutor.awaitTermination(exceptionDelay, TimeUnit.MILLISECONDS);
            LOG.info("Simulator endpoint poller termination complete");
        } catch (InterruptedException e) {
            LOG.error("Error while waiting termination of endpoint poller", e);
            Thread.currentThread().interrupt();
            throw new SimulatorException(e);
        } finally {
            taskExecutor.shutdownNow();
        }
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

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        stop();
    }
}
