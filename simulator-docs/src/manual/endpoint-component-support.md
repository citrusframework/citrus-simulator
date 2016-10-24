## Endpoint component support

We have seen how the simulator handles different transports such as [Http REST](rest-support.md), [SOAP web services](ws-support.md) and [JMS](jms-support.md).
Now the simulator is also able to handle other message transports such as mail communication, JMX mbean server, RMI invocations and much more. The
simulator is able to deal with any kind of endpoint component that is supported in Citrus framework.

The generic endpoint support is added with **@EnableEndpointComponent** annotation.

```java
import com.consol.citrus.simulator.annotation.EnableEndpointComponent;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableEndpointComponent
public class Simulator {
    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

The **@EnableEndpointComponent** annotation performs some auto configuration steps and loads required beans for the Spring application context
in the Spring boot application. Once we use that feature we can have any Citrus endpoint component as inbound source for simulator scenarios. This means
we can have a mail server or a RMI server that is simulated with proper response messages.

As we are using generic endpoint components as inbound source we need to set the endpoint in the configuration. Read about that in the following sections.