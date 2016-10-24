## REST support

The simulator is able to handle REST API calls such as Http GET, POST, PUT, DELETE and so on. The simulator defines a special
REST enabling annotation that we can use on the application class:

```java
import com.consol.citrus.simulator.annotation.EnableRest;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableRest
public class Simulator {
    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

The **@EnableRest** annotation performs some auto configuration steps and loads required beans for the Spring application context
in the Spring boot application.

After that we are ready to handle incoming REST API calls on the simulator. The simulator REST support provides a base REST scenario class.

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

Now the base REST scenario provides the basic receive and send operations for the scenario. Also note that we can use **@RequestMapping** annotations
for advanced [REST request mapping](rest-request-mapping.md).

Besides that you can have several REST related configuration options as described in the following sections.