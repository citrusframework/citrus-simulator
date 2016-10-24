## JMS support

The simulator is able to receive messages from any JMS message broker. The simulator will constantly poll a JMS destination (queue or topic)
for incoming request messages. When the queue is of synchronous nature the simulator is able to send synchronous response messages. The simulator defines a special
JMS enabling annotation that we can use on the application class:

```java
import com.consol.citrus.simulator.annotation.EnableJms;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableJms
public class Simulator {
    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

The **@EnableJms** annotation performs some auto configuration steps and loads required beans for the Spring application context
in the Spring boot application.

With that piece of configuration we are ready to handle incoming JMS messages on the simulator. Of course we need a JMS connection factory and other JMS related 
configuration options as described in the following sections.
