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

package org.citrusframework.simulator;

import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.container.BeforeSuite;
import org.citrusframework.container.SequenceBeforeSuite;
import org.citrusframework.dsl.endpoint.CitrusEndpoints;
import org.citrusframework.mail.client.MailClient;
import org.citrusframework.simulator.sample.Simulator;
import org.citrusframework.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import static org.citrusframework.actions.SendMessageAction.Builder.send;

/**
 * @author Christoph Deppisch
 */
@Test
@Ignore
@ContextConfiguration(classes = SimulatorMailIT.EndpointConfig.class)
public class SimulatorMailIT extends TestNGCitrusSpringSupport {

    /** Test mail client */
    @Autowired
    private MailClient simulatorMailClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Hello</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Hello!</content>" +
                            "</body>" +
                        "</mail-message>"));
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>GoodBye</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say GoodBye!</content>" +
                            "</body>" +
                        "</mail-message>"));
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Default</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Default!</content>" +
                            "</body>" +
                        "</mail-message>"));
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>GoodNight</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say GoodNight!</content>" +
                            "</body>" +
                        "</mail-message>"));

        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>"));

        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>"));

        $(send(simulatorMailClient)
                .message()
                .body("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>"));
    }

    @Configuration
    public static class EndpointConfig {

        @Bean
        public MailClient simulatorMailClient() {
            return CitrusEndpoints.mail().client()
                    .host("localhost")
                    .port(2222)
                    .build();
        }

        @Bean
        @ConditionalOnProperty(name = "simulator.mode", havingValue = "embedded")
        public BeforeSuite startEmbeddedSimulator() {
            return new SequenceBeforeSuite.Builder().actions(context -> SpringApplication.run(Simulator.class)).build();
        }
    }
}
