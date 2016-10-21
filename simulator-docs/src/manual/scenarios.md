## Simulator scenarios

The different scenarios on the simulator describe different response generating classes. Each scenario is capable of receiving a 
incoming request message and is in charge of constructing a proper response message.
 
The scenario gets a name which maps to the incoming request mapping key.
 
```java
@Scenario("Hello")
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

The scenario above is annotated with **@Scenario** which also defines the scenario name. There is one single **configure** method to be implemented.
Here we can use Citrus Java DSL methods. The scenario receives a message and sends some response message to the calling client.

Of course we can use the full Citrus power her in order to construct different message payloads such as XML, JSON, PLAINTEXT and so on.
 
The simulator knows different scenario types for different endpoints:
 
* **SimulatorRestScenario**
* **SimulatorWsScenario**
* **SimulatorJmsScenario**
* **SimulatorEndpointScenario**

You have to pick the respective type according to the simulator endpoint. 