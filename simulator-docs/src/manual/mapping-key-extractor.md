## Mapping key extractor

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

### Configuration

The default mapping logic extracts the root element of incoming requests via Xpath. So each request type gets its own scenario:

```
<successMessage>...</successMessage>
<warningMessage>...</warningMessage>
<errorMessage>...</errorMessage>
```

Given the three messages above the default mapping key extractor would map to scenarios named **successMessage**, **warningMessage** and **errorMessage**.

You can change this mapping behavior by overwriting te default mapping key extractor.

```java
public class SimulatorAdapter extends SimulatorRestAdapter {
    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        HeaderMappingKeyExtractor mappingKeyExtractor = new HeaderMappingKeyExtractor();
        mappingKeyExtractor.setHeaderName("X-simulator-scenario");
        return mappingKeyExtractor;
    } 
}
```

Now the header name **X-simulator-scenario** gets evaluated for each incoming request message. Depending on that header value the respective scenario is executed then.
