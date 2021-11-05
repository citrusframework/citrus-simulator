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

package ${package};

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.mail.client.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

@Test
public class SimulatorMailIT extends TestNGCitrusTestDesigner {

    /** Test mail client */
    @Autowired
    private MailClient simulatorMailClient;

    /**
     * Sends a hello request to server expecting positive response message.
     */
    @CitrusTest
    public void testHelloRequest() {
        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Hello</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Hello!</content>" +
                            "</body>" +
                        "</mail-message>");
    }

    /**
     * Sends goodbye request to server expecting positive response message.
     */
    @CitrusTest
    public void testGoodByeRequest() {
        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>GoodBye</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say GoodBye!</content>" +
                            "</body>" +
                        "</mail-message>");
    }

    /**
     * Sends some other request to server expecting positive default response message.
     */
    @CitrusTest
    public void testDefaultRequest() {
        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Default</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Default!</content>" +
                            "</body>" +
                        "</mail-message>");
    }

    /**
     * Sends some intervening request to server expecting positive response message.
     */
    @CitrusTest
    public void testInterveningRequest() {
        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>GoodNight</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say GoodNight!</content>" +
                            "</body>" +
                        "</mail-message>");

        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>");

        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>");

        send(simulatorMailClient)
                .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                            "<from>user@citrusframework.org</from>" +
                            "<to>citrus@citrusframework.org</to>" +
                            "<cc></cc>" +
                            "<bcc></bcc>" +
                            "<subject>Intervening</subject>" +
                            "<body>" +
                                "<contentType>text/plain; charset=utf-8</contentType>" +
                                "<content>Say Intervening!</content>" +
                            "</body>" +
                        "</mail-message>");
    }
}
