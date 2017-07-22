package com.consol.citrus.simulator.ws;

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

    private XpathMappingDataDictionary receiveDataDictionary = new XpathMappingDataDictionary();
    private XpathMappingDataDictionary sendDataDictionary = new XpathMappingDataDictionary();

    public WsdlOperationScenario(BindingOperation operation) {
        this.operation = operation;

        receiveDataDictionary.getMappings().put("//*/text()/parent::*", "@ignore@");
        receiveDataDictionary.getMappings().put("//*/@*", "@ignore@");

        sendDataDictionary.getMappings().put("//*/text()/parent::*", "citrus:randomString(10)");
        sendDataDictionary.getMappings().put("//*/@*", "citrus:randomNumber(10)");
    }

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario.echo("Scenario WSDL operation: " + operation.getName());
        scenario.echo("${simulator.name}");

        scenario
            .soap()
            .receive()
            .dictionary(receiveDataDictionary)
            .payload(input)
            .soapAction(soapAction);

        scenario
            .soap()
            .send()
            .dictionary(sendDataDictionary)
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
}
