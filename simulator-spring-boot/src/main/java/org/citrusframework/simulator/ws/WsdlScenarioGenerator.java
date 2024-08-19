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

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.simulator.exception.SimulatorException;
import org.citrusframework.spi.CitrusResourceWrapper;
import org.citrusframework.spi.Resource;
import org.citrusframework.xml.schema.locator.JarWSDLLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil.createSampleForType;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * @author Christoph Deppisch
 */
public class WsdlScenarioGenerator implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WsdlScenarioGenerator.class);

    /**
     * Target wsdl to generate scenarios from
     */
    private final Resource wsdlResource;

    /**
     * Naming strategy for generated scenarios
     */
    private WsdlScenarioNamingStrategy namingStrategy = WsdlScenarioNamingStrategy.INPUT;

    /**
     * Default constructor.
     */
    public WsdlScenarioGenerator(SimulatorWebServiceConfigurationProperties simulatorWebServiceConfigurationProperties) {
        wsdlResource = new CitrusResourceWrapper(
            new PathMatchingResourcePatternResolver()
                .getResource(simulatorWebServiceConfigurationProperties.getWsdl().getLocation())
        );
    }

    /**
     * Constructor using wsdl file resource.
     *
     * @param wsdlResource
     */
    public WsdlScenarioGenerator(Resource wsdlResource) {
        this.wsdlResource = wsdlResource;
    }

    private static String getSoapAction(BindingOperation operation) {
        String soapAction = "";
        List extensions = operation.getExtensibilityElements();
        if (extensions != null) {
            for (Object extension : extensions) {
                ExtensibilityElement extElement = (ExtensibilityElement) extension;
                if (extElement instanceof SOAPOperation soapOperation) {
                    soapAction = soapOperation.getSoapActionURI();
                }
            }
        }
        return soapAction;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Assert.notNull(wsdlResource,
            "Missing either WSDL location system property setting or explicit WSDL resource for scenario auto generation");

        Definition wsdl = getWsdlDefinition(wsdlResource);
        XmlObject wsdlObject = compileWsdl(wsdlResource);
        SchemaTypeSystem schemaTypeSystem = compileXsd(wsdlObject);

        for (Object item : wsdl.getBindings().values()) {
            Binding binding = (Binding) item;

            for (Object operationItem : binding.getBindingOperations()) {
                BindingOperation operation = (BindingOperation) operationItem;

                SchemaType requestElem = getSchemaType(schemaTypeSystem, operation.getName(), operation.getOperation().getInput().getName());
                SchemaType responseElem = getSchemaType(schemaTypeSystem, operation.getName(), operation.getOperation().getOutput().getName());

                String soapAction = getSoapAction(operation);
                String scenarioName = switch (namingStrategy) {
                    case INPUT -> operation.getOperation().getInput().getName();
                    case OPERATION -> operation.getOperation().getName();
                    case SOAP_ACTION -> soapAction;
                };

                if (beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
                    logger.info("Register auto generated scenario as bean definition: {}", scenarioName);
                    BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(WsdlOperationScenario.class)
                        .addConstructorArgValue(operation)
                        .addPropertyValue("soapAction", soapAction)
                        .addPropertyValue("input", generateRequest(operation, createSampleForType(requestElem)))
                        .addPropertyValue("output", generateResponse(operation, createSampleForType(responseElem)));

                    if (beanFactory.containsBeanDefinition("inboundXmlDataDictionary")) {
                        beanDefinitionBuilder.addPropertyReference("inboundDataDictionary", "inboundXmlDataDictionary");
                    }

                    if (beanFactory.containsBeanDefinition("outboundXmlDataDictionary")) {
                        beanDefinitionBuilder.addPropertyReference("outboundDataDictionary", "outboundXmlDataDictionary");
                    }

                    beanDefinitionRegistry.registerBeanDefinition(scenarioName, beanDefinitionBuilder.getBeanDefinition());
                } else {
                    logger.info("Register auto generated scenario as singleton: {}", scenarioName);
                    WsdlOperationScenario scenario = createScenario(operation, soapAction, generateRequest(operation, createSampleForType(requestElem)), generateResponse(operation, createSampleForType(responseElem)));
                    beanFactory.registerSingleton(scenarioName, scenario);
                }
            }
        }
    }

    /**
     * Creates the scenario with given WSDL operation information.
     *
     * @param operation
     * @param soapAction
     * @param input
     * @param output
     * @return
     */
    protected WsdlOperationScenario createScenario(BindingOperation operation, String soapAction, String input, String output) {
        return new WsdlOperationScenario(operation)
            .withInput(input)
            .withOutput(output)
            .withSoapAction(soapAction);
    }

    /**
     * Generates request body. Subclasses may add special request body generating logic here.
     *
     * @param operation
     * @param body
     * @return
     */
    protected String generateRequest(BindingOperation operation, String body) {
        return body;
    }

    /**
     * Generates response body. Subclasses may add special response body generating logic here.
     *
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
     *
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
        } catch (WSDLException e) {
            throw new CitrusRuntimeException("Failed to create wsdl schema instance", e);
        }
    }

    /**
     * Compiles WSDL file resource to a XmlObject.
     *
     * @return
     * @throws IOException
     */
    private XmlObject compileWsdl(Resource wsdlResource) {
        try {
            return XmlObject.Factory.parse(wsdlResource.getInputStream(), (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest().setCompileDownloadUrls());
        } catch (XmlException e) {
            for (Object error : e.getErrors()) {
                logger.error(((XmlError) error).getLine() + String.valueOf(error));
            }
            throw new SimulatorException("WSDL could not be parsed", e);
        } catch (Exception e) {
            throw new SimulatorException("WSDL could not be parsed", e);
        }
    }

    /**
     * Finds nested XML schema definition and compiles it to a schema type system instance.
     *
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
            for (int i = 0; i < schemas.length; i++) {
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
                logger.error("Line " + ((XmlError) error).getLine() + ": " + error);
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
        for (int i = 0; i < noNs; i++) {
            int begin = nsWsdlOrig.indexOf("xmlns:", cursor);
            int end = nsWsdlOrig.indexOf("\"", begin + 20);
            namespacesWsdl[i] = nsWsdlOrig.substring(begin, end) + "\"";
            cursor = end;
        }
        return namespacesWsdl;
    }

    /**
     * Finds schema tag and extracts the namespace prefix.
     *
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
        List<String> schemas = new ArrayList<>();
        String openedStartTag = "<" + schemaNsPrefix + "schema";
        String endTag = "</" + schemaNsPrefix + "schema>";

        int cursor = 0;
        while (wsdl.xmlText().indexOf(openedStartTag, cursor) != -1) {
            int begin = wsdl.xmlText().indexOf(openedStartTag, cursor);
            int end = wsdl.xmlText().indexOf(endTag, begin) + endTag.length();
            int insertPointNamespacesWsdl = wsdl.xmlText().indexOf(" ", begin);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(wsdl.xmlText(), begin, insertPointNamespacesWsdl).append(" ");

            for (String nsWsdl : namespacesWsdl) {
                String nsPrefix = nsWsdl.substring(0, nsWsdl.indexOf("="));
                if (!wsdl.xmlText().substring(begin, end).contains(nsPrefix)) {
                    stringBuilder.append(nsWsdl).append(" ");
                }
            }

            stringBuilder.append(wsdl.xmlText(), insertPointNamespacesWsdl, end);
            schemas.add(stringBuilder.toString());
            cursor = end;
        }

        return schemas.toArray(new String[]{});
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

    /**
     * Enum representing different kinds of scenario naming.
     */
    public enum WsdlScenarioNamingStrategy {
        INPUT,
        OPERATION,
        SOAP_ACTION
    }
}
