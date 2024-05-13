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

package org.citrusframework.simulator.ws;

import org.citrusframework.message.MessageHeaders;
import org.citrusframework.simulator.dictionary.InboundXmlDataDictionary;
import org.citrusframework.simulator.dictionary.OutboundXmlDataDictionary;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.ScenarioRunner;

import javax.wsdl.BindingOperation;

import static org.citrusframework.actions.EchoAction.Builder.echo;

public class WsdlOperationScenario extends AbstractSimulatorScenario {

    /** Operation in wsdl */
    private final BindingOperation operation;

    /** Optional soap action */
    private String soapAction;

    /** Input and output messages */
    private String input;
    private String output;

    private InboundXmlDataDictionary inboundDataDictionary;
    private OutboundXmlDataDictionary outboundDataDictionary;

    /**
     * Default constructor.
     * @param operation
     */
    public WsdlOperationScenario(BindingOperation operation) {
        this.operation = operation;
    }

    @Override
    public void run(ScenarioRunner scenario) {
        scenario.$(echo("Generated scenario from WSDL operation: " + operation.getName()));

        scenario.$(scenario.soap()
            .receive()
            .message()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true)
            .dictionary(inboundDataDictionary)
            .body(input)
            .soapAction(soapAction));

        scenario.$(scenario.soap()
            .send()
            .message()
            .header(MessageHeaders.MESSAGE_PREFIX + "generated", true)
            .dictionary(outboundDataDictionary)
            .body(output));
    }

    /**
     * Sets the soap action for this operation.
     * @param soapAction
     * @return
     */
    public WsdlOperationScenario withSoapAction(String soapAction) {
        this.soapAction = soapAction;
        return this;
    }

    /**
     * Sets the operation input body.
     * @param input
     * @return
     */
    public WsdlOperationScenario withInput(String input) {
        this.input = input;
        return this;
    }

    /**
     * Sets the operation output body.
     * @param output
     * @return
     */
    public WsdlOperationScenario withOutput(String output) {
        this.output = output;
        return this;
    }

    /**
     * Gets the operation.
     *
     * @return
     */
    public BindingOperation getOperation() {
        return operation;
    }

    /**
     * Gets the soapAction.
     *
     * @return
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * Sets the soapAction.
     *
     * @param soapAction
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * Gets the input.
     *
     * @return
     */
    public String getInput() {
        return input;
    }

    /**
     * Sets the input.
     *
     * @param input
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Gets the output.
     *
     * @return
     */
    public String getOutput() {
        return output;
    }

    /**
     * Sets the output.
     *
     * @param output
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Gets the inboundDataDictionary.
     *
     * @return
     */
    public InboundXmlDataDictionary getInboundDataDictionary() {
        return inboundDataDictionary;
    }

    /**
     * Sets the inboundDataDictionary.
     *
     * @param inboundDataDictionary
     */
    public void setInboundDataDictionary(InboundXmlDataDictionary inboundDataDictionary) {
        this.inboundDataDictionary = inboundDataDictionary;
    }

    /**
     * Gets the outboundDataDictionary.
     *
     * @return
     */
    public OutboundXmlDataDictionary getOutboundDataDictionary() {
        return outboundDataDictionary;
    }

    /**
     * Sets the outboundDataDictionary.
     *
     * @param outboundDataDictionary
     */
    public void setOutboundDataDictionary(OutboundXmlDataDictionary outboundDataDictionary) {
        this.outboundDataDictionary = outboundDataDictionary;
    }
}
