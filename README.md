[1] Citrus Simulator
================

This is a standalone SOAP simulator for any kind of SOAP WebService interface.
WebService clients invoke the server endpoint and the simulator answers with predefined response
message logic.

Message processing
---------

First of all the simulator identifies the request operation called on the endpoint and maps to the test logic defined
for this use case scenario.

There are multiple ways to identify the simulator use case form incoming request messages:

* Message-Type: Each request message type (XML root QName) results in a separate simulator use case
* SOAP Action: Each SOAP action value defines a simulator use case
* Message Header: Any SOAP or Http message header value specifies a new simulator use case
* XPath payload: An XPath expression is evaluated on the message payload to identify the use case

Once the simulator use case is identified the respective test logic builder is executed. The Citrus test logic provides
proper SOAP response messages as a result to the calling client. The response messages can hold dynamic values and the
simulator is able to perform complex testing logic. The test logic is built in Java classes that use the Citrus test
DSL for defining the simulator use case steps.

Features
---------

* Implements any SOAP interface (WSDL)

* Powerful SOAP response simulation (dynamic identifiers, SOAP fault simulation, etc.)

* Easy message definition (Citrus Java DSL)

* XSD schema validation of incoming requests

* Powerful header support (SOAP Action, SOAP header fragments, Http headers)

Quick start
---------

* Unpack project

* Build all sources from Maven (mvn clean install)

* See package com.consol.citrus.simulator.builder for sample test builder classes

* Run embedded Jetty from Maven (mvn jetty:run)

* Execute SimulatorClient.java and see test requests get handled by simulator

Resources
---------

Following code resources build the simulator core logic:

* Test builders: Group of Java classes (@Component Spring Bean Annotation) for all use case scenarios.

* Servlet configuration (src/main/webapp/WEB-INF/citrus-servlet-context.xml): SpringWS beans defining how use cases are
identified and mapped to test builders.

* Simulator configuration (src/main/resources/META-INF/citrus-simulator-context.xml): Citrus Spring configuration for
sending/receiving SOAP messages

Information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: http://www.citrusframework.org/images/brand_logo.png "Citrus"
 [2]: http://www.citrusframework.org
 [3]: http://www.citrusframework.org/reference/html/
