Citrus JMS Simulator ![Logo][1]
================

This is a standalone simulator application for JMS messaging.

Clients are able to access the simulator endpoints and the simulator responds with predefined response
messages according to its scenarios. The simulator response logic is very powerful and enables us to simulate 
any kind of server interface.

Read the simulator [user manual](https://citrusframework.org/citrus-simulator/) for more information.

Message processing
---------

First of all the simulator identifies the simulator scenario based on a mapping key that is extracted from the incoming request. Based
on that operation key the respective simulator scenario is executed.

There are multiple ways to identify the simulator scenario from incoming request messages:

* Message-Type: Each request message type (XML root QName) results in a separate simulator scenario
* REST request mappings: Identifies the scenario based on Http method and resource path on server
* SOAP Action: Each SOAP action value defines a simulator scenario
* Message Header: Any SOAP or Http message header value specifies a new simulator scenario
* XPath payload: An XPath expression is evaluated on the message payload to identify the scenario

Once the simulator scenario is identified the respective test logic builder is executed. The Citrus test logic provides
proper response messages as a result to the calling client. The response messages can hold dynamic values and the
simulator is able to perform complex response generating logic. The test logic is built in Java classes that use the Citrus test
DSL for defining the simulator scenario steps.

Quick start
---------

You can build the simulator application locally with Maven:

```
mvn clean install
```

This will compile and package all resources for you. Also some prepared Citrus integration tests are executed during the build. 
After the successful build you are able to run the simulator with:

```
mvn spring-boot:run
```

Open your browser and point to [http://localhost:8080](http://localhost:8080). You will see the simulator user interface with all available scenarios and 
latest activities. 

You can execute the Citrus integration tests now in order to get some interaction with the simulator. Open the Maven project in your favorite IDE and
run the tests with TestNG plugins. You should see the tests calling operations on the simulator in order to receive proper responses. The simulator user interface should track those
interactions accordingly.

Information
---------

Read the [user manual](https://citrusframework.org/citrus-simulator/) for detailed instructions and features.
For more information on Citrus see [citrusframework.org][2], including a complete [reference manual][3].

 [1]: https://citrusframework.org/img/brand-logo.png "Citrus"
 [2]: https://citrusframework.org
 [3]: https://citrusframework.org/reference/html/
