Citrus Simulator ![Logo][1]
================

This is a standalone simulator application for different message transports such as Http REST APIs, SOAP WebService interface and
JMS messaging.

Clients are able to access the simulator endpoints and the simulator answers with predefined response
messages. The simulator response logic is very powerful and enables us to simulate any kind of server interface.

Message processing
---------

First of all the simulator identifies the request operation called on the endpoint and maps to the test logic defined
for this simulator scenario.

There are multiple ways to identify the simulator scenario form incoming request messages:

* Message-Type: Each request message type (XML root QName) results in a separate simulator scenario
* REST request mappings: Identifies the scenario based on Http method and resource path on server
* SOAP Action: Each SOAP action value defines a simulator scenario
* Message Header: Any SOAP or Http message header value specifies a new simulator scenario
* XPath payload: An XPath expression is evaluated on the message payload to identify the scenario

Once the simulator scenario is identified the respective test logic builder is executed. The Citrus test logic provides
proper response messages as a result to the calling client. The response messages can hold dynamic values and the
simulator is able to perform complex response generating logic. The test logic is built in Java classes that use the Citrus test
DSL for defining the simulator scenario steps.

Features
---------

* Implements any of the interfaces: SOAP (WSDL), REST, JMS, File
* Powerful response simulation (dynamic identifiers, fault simulation, value extraction, etc.)
* Easy message definition (Citrus Java DSL)
* XSD schema validation of incoming requests
* JSON message format handling
* Powerful header support (SOAP Action, SOAP header fragments, Http headers)

Quick start
---------

* Unpack project
* Build all sources from Maven (mvn clean install)
* See package com.consol.citrus.simulator.scenario for sample test builder classes
* Run Spring Boot application from Maven (mvn spring-boot:run)
* Execute Citrus client tests and see simulator respond to that

Resources
---------

Following code resources build the simulator core logic:

* SimulatorScenario: Group of Java classes (@Component Spring Bean Annotation) for all scenario scenarios.
* Annotation based Spring Boot application configuration
* Simulator configuration as part of the Spring application context configuration

Information
---------

For more information on Citrus see [www.citrusframework.org][2], including
a complete [reference manual][3].

 [1]: http://www.citrusframework.org/img/brand-logo.png "Citrus"
 [2]: http://www.citrusframework.org
 [3]: http://www.citrusframework.org/reference/html/
