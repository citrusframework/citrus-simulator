package com.consol.citrus.simulator.ws;

import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.dictionary.InboundXmlDataDictionary;
import com.consol.citrus.simulator.dictionary.OutboundXmlDataDictionary;
import com.consol.citrus.simulator.scenario.AbstractSimulatorScenario;
import com.consol.citrus.simulator.scenario.ScenarioDesigner;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;

import javax.wsdl.BindingOperation;

/**
 * @author Christoph Deppisch
 */
public class WsdlOperationScenario extends AbstractSimulatorScenario {

    /** Operation in wsdl */
    private final BindingOperation operation;

    /** Optional soap action */
    private String soapAction;

    /** Input and output messages */
    private String input;
    private String output;

    private XpathMappingDataDictionary inboundDataDictionary;
    private XpathMappingDataDictionary outboundDataDictionary;

    /**
     * Default constructor.
     * @param operation
     */
    public WsdlOperationScenario(BindingOperation operation, SimulatorConfigurationProperties simulatorConfiguration) {
        this.operation = operation;

        inboundDataDictionary = new InboundXmlDataDictionary(simulatorConfiguration);
        outboundDataDictionary = new OutboundXmlDataDictionary(simulatorConfiguration);
    }

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.echo("Generated scenario from WSDL operation: " + operation.getName());

        scenario
            .soap()
            .receive()
            .dictionary(inboundDataDictionary)
            .payload(input)
            .soapAction(soapAction);

        scenario
            .soap()
            .send()
            .dictionary(outboundDataDictionary)
            .payload(output);
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
    public XpathMappingDataDictionary getInboundDataDictionary() {
        return inboundDataDictionary;
    }

    /**
     * Sets the inboundDataDictionary.
     *
     * @param inboundDataDictionary
     */
    public void setInboundDataDictionary(XpathMappingDataDictionary inboundDataDictionary) {
        this.inboundDataDictionary = inboundDataDictionary;
    }

    /**
     * Gets the outboundDataDictionary.
     *
     * @return
     */
    public XpathMappingDataDictionary getOutboundDataDictionary() {
        return outboundDataDictionary;
    }

    /**
     * Sets the outboundDataDictionary.
     *
     * @param outboundDataDictionary
     */
    public void setOutboundDataDictionary(XpathMappingDataDictionary outboundDataDictionary) {
        this.outboundDataDictionary = outboundDataDictionary;
    }
}
