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

After that we are ready to handle incoming REST API calls on the simulator. In addition to that you are able to add REST related configuration options 
and request mappings as described in the following sections.
