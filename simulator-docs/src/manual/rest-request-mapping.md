## REST request mapping

Usually we define simulator scenarios and map them to incoming requests by their names. When using REST support on the simulator we can also
use request mapping annotations on scenarios in order to map incoming requests.

This looks like follows:

```java
@Scenario("Hello")
@RequestMapping(value = "/services/rest/simulator/hello", method = RequestMethod.POST)
public class HelloScenario extends SimulatorRestScenario {

    @Override
    protected void configure() {
        scenario()
            .receive()
            .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say Hello!" +
                     "</Hello>");

        scenario()
            .send()
            .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Hi there!" +
                     "</HelloResponse>");
    }
}
```

As you can see the example above uses **@RequestMapping** annotation in addition to the **@Scenario** annotation. All requests on the request path
**/services/rest/simulator/hello** of method **POST** will be mapped to the scenario. With this strategy the simulator is able to map requests based
on methods, request paths and parameters.

The mapping strategy requires a special mapping key extractor implementation that automatically scans for scenarios with **@RequestMapping** annotations.
The **AnnotationRequestMappingKeyExtractor** is active by default so in case you need to apply different mapping strategies you must overwrite the mapping key extractor
in configuration adapter.
