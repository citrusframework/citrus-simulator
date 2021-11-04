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

package org.citrusframework.simulator;

import java.util.Arrays;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerBeforeSuiteSupport;
import com.consol.citrus.dsl.runner.TestRunnerBeforeTestSupport;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.citrusframework.simulator.model.ScenarioParameter;
import org.citrusframework.simulator.sample.jms.async.Simulator;
import org.citrusframework.simulator.sample.jms.async.model.FaxStatusEnumType;
import org.citrusframework.simulator.sample.jms.async.model.FaxType;
import org.citrusframework.simulator.sample.jms.async.scenario.PayloadHelper;
import org.citrusframework.simulator.sample.jms.async.variables.ReferenceId;
import org.citrusframework.simulator.sample.jms.async.variables.Status;
import org.citrusframework.simulator.sample.jms.async.variables.StatusMessage;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

/**
 * @author Martin Maher
 */
@Test
@ContextConfiguration(classes = SimulatorJmsFaxIT.EndpointConfig.class)
public class SimulatorJmsFaxIT extends TestNGCitrusTestDesigner {
    private PayloadHelper payloadHelper = new PayloadHelper();

    @Autowired
    @Qualifier("simulatorInboundEndpoint")
    private JmsEndpoint simulatorInboundEndpoint;

    @Autowired
    @Qualifier("simulatorStatusEndpoint")
    private JmsEndpoint simulatorStatusEndpoint;

    @Autowired
    @Qualifier("simulatorRestEndpoint")
    protected HttpClient restEndpoint;

    /**
     * Tests the (Default) FaxQueued simulation scenario
     */
    @CitrusTest
    public void testFaxQueuedScenario() {
        ReferenceId referenceId = new ReferenceId();

        FaxType fax = payloadHelper.createFaxType("Joe Bloggs", "Testing the default scenario", "01-223344", "01-556677");

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateSendFaxMessage("Non-Matchable Scenario", fax, referenceId.getValue(), true),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller());

        // check no other status messages are sent; the default scenario only sends one status message
        receiveTimeout(simulatorStatusEndpoint)
                .timeout(3000);
    }

    /**
     * Tests the FaxSent simulation scenario
     */
    @CitrusTest
    public void testFaxSentScenario() {
        ReferenceId referenceId = new ReferenceId();

        FaxType fax = payloadHelper.createFaxType("Joe Bloggs", "Testing the FaxSent scenario", "01-223344", "01-556677");

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateSendFaxMessage("FaxSent", fax, referenceId.getValue(), true),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller());

        sleep(2000L);

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.SUCCESS, "The fax message has been successfully sent"),
                        payloadHelper.getMarshaller());
    }

    /**
     * Tests the FaxCancelled simulation scenario
     */
    @CitrusTest
    public void testFaxCancelledScenario() {
        ReferenceId referenceId = new ReferenceId();

        FaxType fax = payloadHelper.createFaxType("Joe Bloggs", "Testing the FaxCancelled scenario", "01-223344", "01-556677");

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateSendFaxMessage("FaxCancelled", fax, referenceId.getValue(), true),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller());

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateCancelFaxMessage(referenceId.getValue()),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.CANCELLED, "The fax message has been cancelled"),
                        payloadHelper.getMarshaller());
    }

    /**
     * Tests the FaxBusy simulation scenario
     */
    @CitrusTest
    public void testFaxBusyScenario() {
        ReferenceId referenceId = new ReferenceId();

        FaxType fax = payloadHelper.createFaxType("Joe Bloggs", "Testing the FaxBusy scenario", "01-223344", "01-556677");

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateSendFaxMessage("FaxBusy", fax, referenceId.getValue(), true),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.ERROR, "Error transmitting fax: The receiving fax was busy"),
                        payloadHelper.getMarshaller());
    }

    /**
     * Tests the FaxNoAnswer simulation scenario
     */
    @CitrusTest
    public void testFaxNoAnswerScenario() {
        ReferenceId referenceId = new ReferenceId();

        FaxType fax = payloadHelper.createFaxType("Joe Bloggs", "Testing the FaxNoAnswer scenario", "01-223344", "01-556677");

        send(simulatorInboundEndpoint)
                .payload(payloadHelper.generateSendFaxMessage("FaxNoAnswer", fax, referenceId.getValue(), true),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, "The fax message has been queued and will be send shortly"),
                        payloadHelper.getMarshaller());

        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.ERROR, "Error transmitting fax: No answer from the receiving fax"),
                        payloadHelper.getMarshaller());
    }

    /**
     * Tests the UpdateFaxStatus simulation starter. It launches the simulation started via
     * the simulator's REST interface and verifies that the status update was sent.
     */
    @CitrusTest
    public void testUpdateFaxStatusStarter() {
        ReferenceId referenceId = new ReferenceId();
        Status status = new Status(FaxStatusEnumType.QUEUED);
        StatusMessage statusMessage = new StatusMessage("The fax message has been queued and will be send shortly");

        http()
            .client(restEndpoint)
            .send()
            .post("/api/scenario/launch/UpdateFaxStatus")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .payload(asJson(referenceId.asScenarioParameter(),
                    status.asScenarioParameter(),
                    statusMessage.asScenarioParameter())
            );

        http()
            .client(restEndpoint)
            .receive().response(HttpStatus.OK);


        receive(simulatorStatusEndpoint)
                .payload(payloadHelper.generateFaxStatusMessage(referenceId.getValue(), FaxStatusEnumType.QUEUED, statusMessage.getValue()),
                        payloadHelper.getMarshaller());

    }

    private String asJson(ScenarioParameter... scenarioParameters) {
        final Jackson2JsonObjectMapper mapper = new Jackson2JsonObjectMapper();
        try {
            return mapper.toJson(Arrays.asList(scenarioParameters));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Configuration
    public static class EndpointConfig {
        @Bean
        public XsdSchemaRepository schemaRepository() {
            XsdSchemaRepository schemaRepository = new XsdSchemaRepository();
            schemaRepository.getLocations().add("classpath:xsd/FaxGatewayService.xsd");
            return schemaRepository;
        }

        @Bean(initMethod = "start", destroyMethod = "stop")
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BrokerService messageBroker() throws Exception {
            BrokerService brokerService = new BrokerService();
            brokerService.setPersistent(false);
            brokerService.addConnector("tcp://localhost:61616");
            return brokerService;
        }

        @Bean
        public ActiveMQConnectionFactory connectionFactory() {
            return new ActiveMQConnectionFactory("tcp://localhost:61616");
        }

        @Bean
        public JmsEndpoint simulatorInboundEndpoint() {
            return CitrusEndpoints.jms()
                    .asynchronous()
                    .connectionFactory(connectionFactory())
                    .destination("Fax.Inbound")
                    .build();
        }

        @Bean
        public JmsEndpoint simulatorStatusEndpoint() {
            return CitrusEndpoints.jms()
                    .asynchronous()
                    .connectionFactory(connectionFactory())
                    .destination("Fax.Status")
                    .build();
        }

        @Bean
        public HttpClient simulatorRestEndpoint() {
            return CitrusEndpoints.http().client()
                    .requestUrl(String.format("http://localhost:%s", 8080))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }

        @Bean
        public TestRunnerBeforeTestSupport purgeJmsQueues() {
            return new TestRunnerBeforeTestSupport() {
                @Override
                public void beforeTest(TestRunner runner) {
                    runner.purgeQueues(builder -> {
                       builder.connectionFactory(connectionFactory());
                       builder.queueNames("Fax.Inbound", "Fax.Status");
                    });

                }
            };
        }

        @Bean
        @DependsOn("messageBroker")
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public TestRunnerBeforeSuiteSupport startEmbeddedSimulator() {
            return new TestRunnerBeforeSuiteSupport() {
                @Override
                public void beforeSuite(TestRunner runner) {
                    SpringApplication.run(Simulator.class);
                }
            };
        }
    }
}
