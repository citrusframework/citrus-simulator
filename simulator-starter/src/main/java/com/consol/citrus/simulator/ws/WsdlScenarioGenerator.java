package com.consol.citrus.simulator.ws;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.xml.schema.locator.JarWSDLLocator;
import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class WsdlScenarioGenerator implements BeanFactoryPostProcessor {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(WsdlScenarioGenerator.class);

    /** Target wsdl to generate scenarios from */
    private final Resource wsdlResource;

    /** Naming strategy for generated scenarios */
    private WsdlScenarioNamingStrategy namingStrategy = WsdlScenarioNamingStrategy.INPUT;

    /** Simulator configuration */
    private SimulatorConfigurationProperties simulatorConfiguration;

    /**
     * Enum representing different kinds of scenario naming.
     */
    public enum WsdlScenarioNamingStrategy {
        INPUT,
        OPERATION,
        SOAP_ACTION
    }

    /**
     * Constructor using wsdl file resource.
     * @param wsdlResource
     */
    public WsdlScenarioGenerator(Resource wsdlResource) {
        this.wsdlResource = wsdlResource;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Definition wsdl = getWsdlDefinition(wsdlResource);
        XmlObject wsdlObject = compileWsdl(wsdlResource);
        SchemaTypeSystem schemaTypeSystem = compileXsd(wsdlObject);

        for (Object item : wsdl.getBindings().values()) {
            Binding binding = (Binding) item;

            for (Object operationItem : binding.getBindingOperations()) {
                BindingOperation operation = (BindingOperation) operationItem;

                SchemaType requestElem = getSchemaType(schemaTypeSystem, operation.getName(), operation.getOperation().getInput().getName());
                SchemaType responseElem = getSchemaType(schemaTypeSystem, operation.getName(), operation.getOperation().getOutput().getName());

                String soapAction = "";
                List extensions = operation.getExtensibilityElements();
                if (extensions != null) {
                    for (int i = 0; i < extensions.size(); i++) {
                        ExtensibilityElement extElement = (ExtensibilityElement) extensions.get(i);
                        if (extElement instanceof SOAPOperation) {
                            SOAPOperation soapOp = (SOAPOperation) extElement;
                            soapAction = soapOp.getSoapActionURI();
                        }
                    }
                }

                String scenarioName;
                switch (namingStrategy) {
                    case INPUT:
                        scenarioName = operation.getOperation().getInput().getName();
                        break;
                    case OPERATION:
                        scenarioName = operation.getOperation().getName();
                        break;
                    case SOAP_ACTION:
                        scenarioName = soapAction;
                        break;
                    default:
                        throw new SimulatorException("Unknown scenario naming strategy");
                }

                beanFactory.registerSingleton(scenarioName, createScenario(operation, soapAction, generateRequest(operation, SampleXmlUtil.createSampleForType(requestElem)), generateResponse(operation, SampleXmlUtil.createSampleForType(responseElem))));
            }
        }
    }

    /**
     * Creates the scenario with given WSDL operation information.
     * @param operation
     * @param soapAction
     * @param input
     * @param output
     * @return
     */
    protected WsdlOperationScenario createScenario(BindingOperation operation, String soapAction, String input, String output) {
        return new WsdlOperationScenario(operation, simulatorConfiguration)
                .withInput(input)
                .withOutput(output)
                .withSoapAction(soapAction);
    }

    /**
     * Generates request body. Subclasses may add special request body generating logic here.
     * @param operation
     * @param body
     * @return
     */
    protected String generateRequest(BindingOperation operation, String body) {
        return body;
    }

    /**
     * Generates response body. Subclasses may add special response body generating logic here.
     * @param operation
     * @param body
     * @return
     */
    protected String generateResponse(BindingOperation operation, String body) {
        return body;
    }

    /**
     * @param schemaTypeSystem
     * @param operation
     * @param elementName
     * @return
     */
    private SchemaType getSchemaType(SchemaTypeSystem schemaTypeSystem, String operation, String elementName) {
        for (SchemaType elem : schemaTypeSystem.documentTypes()) {
            if (elem.getContentModel().getName().getLocalPart().equals(elementName)) {
                return elem;
            }
        }

        throw new SimulatorException("Unable to find schema type declaration '" + elementName + "'" +
                " for WSDL operation '" + operation + "'");
    }

    /**
     * Reads WSDL definition from resource.
     * @param wsdl
     * @return
     * @throws IOException
     * @throws WSDLException
     */
    private Definition getWsdlDefinition(Resource wsdl) {
        try {
            Definition definition;
            if (wsdl.getURI().toString().startsWith("jar:")) {
                // Locate WSDL imports in Jar files
                definition = WSDLFactory.newInstance().newWSDLReader().readWSDL(new JarWSDLLocator(wsdl));
            } else {
                definition = WSDLFactory.newInstance().newWSDLReader().readWSDL(wsdl.getURI().getPath(), new InputSource(wsdl.getInputStream()));
            }

            return definition;
        } catch (IOException e) {
            throw new CitrusRuntimeException("Failed to read wsdl file resource", e);
        } catch (WSDLException e) {
            throw new CitrusRuntimeException("Failed to create wsdl schema instance", e);
        }
    }

    /**
     * Compiles WSDL file resource to a XmlObject.
     * @return
     * @throws IOException
     */
    private XmlObject compileWsdl(Resource wsdlResource) {
        try {
            File wsdlFile = wsdlResource.getFile();
            return XmlObject.Factory.parse(wsdlFile, (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest().setCompileDownloadUrls());
        } catch (XmlException e) {
            for (Object error : e.getErrors()) {
                log.error(((XmlError)error).getLine() + "" + error.toString());
            }
            throw new SimulatorException("WSDL could not be parsed", e);
        } catch (Exception e) {
            throw new SimulatorException("WSDL could not be parsed", e);
        }
    }

    /**
     * Finds nested XML schema definition and compiles it to a schema type system instance.
     * @param wsdl
     * @return
     */
    private SchemaTypeSystem compileXsd(XmlObject wsdl) {
        String[] namespacesWsdl = extractNamespacesOnWsdlLevel(wsdl);
        String schemaNsPrefix = extractSchemaNamespacePrefix(wsdl);

        // extract each schema-element and add missing namespaces defined on wsdl-level
        String[] schemas = getNestedSchemas(wsdl, namespacesWsdl, schemaNsPrefix);

        XmlObject[] xsd = new XmlObject[schemas.length];
        try {
            for (int i=0; i < schemas.length; i++) {
                xsd[i] = XmlObject.Factory.parse(schemas[i], (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest().setCompileDownloadUrls());
            }
        } catch (Exception e) {
            throw new SimulatorException("Failed to parse XSD schema", e);
        }

        SchemaTypeSystem schemaTypeSystem = null;
        try {
            schemaTypeSystem = XmlBeans.compileXsd(xsd, XmlBeans.getContextTypeLoader(), new XmlOptions());
        } catch (XmlException e) {
            for (Object error : e.getErrors()) {
                log.error("Line " + ((XmlError)error).getLine() + ": " + error.toString());
            }
            throw new SimulatorException("Failed to compile XSD schema", e);
        } catch (Exception e) {
            throw new SimulatorException("Failed to compile XSD schema", e);
        }
        return schemaTypeSystem;
    }

    /**
     * Returns an array of all namespace declarations, found on wsdl-level.
     *
     * @param wsdl
     * @return
     */
    private String[] extractNamespacesOnWsdlLevel(XmlObject wsdl) {
        int cursor = wsdl.xmlText().indexOf(":") + ":definitions ".length();
        String nsWsdlOrig = wsdl.xmlText().substring(cursor, wsdl.xmlText().indexOf(">", cursor));
        int noNs = StringUtils.countOccurrencesOf(nsWsdlOrig, "xmlns:");
        String[] namespacesWsdl = new String[noNs];
        cursor = 0;
        for (int i=0; i<noNs; i++) {
            int begin = nsWsdlOrig.indexOf("xmlns:", cursor);
            int end = nsWsdlOrig.indexOf("\"", begin + 20);
            namespacesWsdl[i] = nsWsdlOrig.substring(begin, end) + "\"";
            cursor = end;
        }
        return namespacesWsdl;
    }

    /**
     * Finds schema tag and extracts the namespace prefix.
     * @param wsdl
     * @return
     */
    private String extractSchemaNamespacePrefix(XmlObject wsdl) {
        String schemaNsPrefix = "";
        if (wsdl.xmlText().contains(":schema")) {
            int cursor = wsdl.xmlText().indexOf(":schema");
            for (int i = cursor; i > cursor - 100; i--) {
                schemaNsPrefix = wsdl.xmlText().substring(i, cursor);
                if (schemaNsPrefix.startsWith("<")) {
                    return schemaNsPrefix.substring(1) + ":";
                }
            }
        }
        return schemaNsPrefix;
    }

    /**
     * Finds nested schema definitions and puts globally WSDL defined namespaces to schema level.
     *
     * @param wsdl
     * @param namespacesWsdl
     * @param schemaNsPrefix
     */
    private String[] getNestedSchemas(XmlObject wsdl, String[] namespacesWsdl, String schemaNsPrefix) {
        List<String> schemas = new ArrayList<String>();
        String openedStartTag = "<" + schemaNsPrefix + "schema";
        String endTag = "</" + schemaNsPrefix + "schema>";

        int cursor = 0;
        while (wsdl.xmlText().indexOf(openedStartTag, cursor) != -1) {
            int begin = wsdl.xmlText().indexOf(openedStartTag, cursor);
            int end = wsdl.xmlText().indexOf(endTag, begin) + endTag.length();
            int insertPointNamespacesWsdl = wsdl.xmlText().indexOf(" ", begin);

            StringBuffer buf = new StringBuffer();
            buf.append(wsdl.xmlText().substring(begin, insertPointNamespacesWsdl)).append(" ");

            for (String nsWsdl : namespacesWsdl) {
                String nsPrefix = nsWsdl.substring(0, nsWsdl.indexOf("="));
                if (!wsdl.xmlText().substring(begin, end).contains(nsPrefix)) {
                    buf.append(nsWsdl).append(" ");
                }
            }

            buf.append(wsdl.xmlText().substring(insertPointNamespacesWsdl, end));
            schemas.add(buf.toString());
            cursor = end;
        }

        return schemas.toArray(new String[] {});
    }

    /**
     * Gets the simulatorConfiguration.
     *
     * @return
     */
    public SimulatorConfigurationProperties getSimulatorConfiguration() {
        return simulatorConfiguration;
    }

    /**
     * Sets the simulatorConfiguration.
     *
     * @param simulatorConfiguration
     */
    public void setSimulatorConfiguration(SimulatorConfigurationProperties simulatorConfiguration) {
        this.simulatorConfiguration = simulatorConfiguration;
    }

    /**
     * Gets the namingStrategy.
     *
     * @return
     */
    public WsdlScenarioNamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    /**
     * Sets the namingStrategy.
     *
     * @param namingStrategy
     */
    public void setNamingStrategy(WsdlScenarioNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

}
