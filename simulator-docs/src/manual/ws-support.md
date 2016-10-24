## Web Service support

The simulator is able to handle SOAP Web Service calls as a server. The simulator defines a special
SOAP enabling annotation that we can use on the application class:

```java
import com.consol.citrus.simulator.annotation.EnableWebService;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableWebService
public class Simulator {
    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

The **@EnableWebService** annotation performs some auto configuration steps and loads required beans for the Spring application context
in the Spring boot application.

After that we are ready to handle incoming SOAP Web Service calls on the simulator. We can use the default scenario base class for SOAP Web Services.

```java
@Scenario("Hello")
public class HelloScenario extends SimulatorWebServiceScenario {

    @Override
    protected void configure() {
        scenario()
            .receive()
            .payload("<Hello xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Say Hello!" +
                     "</Hello>")
            .header(SoapMessageHeaders.SOAP_ACTION, "Hello");

        scenario()
            .send()
            .payload("<HelloResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Hi there!" +
                     "</HelloResponse>");
    }
}
```

The **SimulatorWebServiceScenario** automatically handles the SOAP envelope so we do not have to deal with that in the scenario receive and send operations. Also
the scenario receive operation has access to the SOAP action of the incoming request call. Besides that we can also [return a SOAP fault](ws-soap-faults.md) message as scenario outcome. 

Let's move on with having a look at the SOAP related configuration options as described in the following sections.
