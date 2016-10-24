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

After that we are ready to handle incoming SOAP Web Service calls on the simulator. In addition to that you are able to add SOAP related configuration options
as described in the following sections.
