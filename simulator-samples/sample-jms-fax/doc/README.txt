This sample project demonstrates how to simulate an application which uses the JMS messaging standard for commication. The simulator uses the asynchronous communication pattern for receiving and sending XML messages.

The simulator simulates a FAX Gateway which provides a service for sending and receiving faxes. The following four scenarios will be simulated:


TODO Table


A simulator always needs some way of determining which simulation scenario should be started based on the incoming message. For this simulator we use the clientId.

TODO Sample Request

The following XPath expression is used for extracting the clientId from the incoming message:

TODO Xpath

The simulator compares this value with the scenario name. When a match is found the simulation scenario is started. Otherwise the default scenario is started.


 within the


