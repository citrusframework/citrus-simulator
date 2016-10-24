## SOAP faults

The simulator is in charge of sending proper response messages to the calling client. When using SOAP we might also want to send
back a SOAP fault message. Therefore the default Web Service scenario implementation also provides fault responses as scenario result.

```java
@Scenario("GoodNight")
public class GoodNightScenario extends SimulatorWebServiceScenario {

    @Override
    protected void configure() {
        scenario()
            .receive()
            .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Go to sleep!" +
                     "</GoodNight>")
            .header(SoapMessageHeaders.SOAP_ACTION, "GoodNight");

        scenario()
            .sendFault()
            .faultCode("{http://citrusframework.org}CITRUS:SIM-1001")
            .faultString("No sleep for me!");
    }
}
```

The example above shows a simple fault generating SOAP scenario. The base class **SimulatorWebServiceScenario** provides
the **sendFault()** method in order to create proper SOAP fault messages. The simulator automatically add SOAP envelope and SOAP fault
message details for you. So we can decide wheather to provide a success response or SOAP fault.