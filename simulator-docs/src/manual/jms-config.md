## JMS configuration

Once the JMS support is enabled on the simulator we have different configuration options. The most comfortable way is to
add a **SimulatorJmsAdapter** implementation to the classpath. The adapter provides several configuration methods.

```java
public abstract class SimulatorJmsAdapter implements SimulatorJmsConfigurer {
    @Override
    public ConnectionFactory connectionFactory() {
        return new SingleConnectionFactory();
    }

    @Override
    public String destinationName() {
        return System.getProperty("citrus.simulator.jms.destination", "Citrus.Simulator.Inbound");
    }

    @Override
    public boolean useSoapEnvelope() {
        return false;
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }
}
```   

The adapter defines methods that configure the simulator JMS handling. For instance we can add another mapping key extractor implementation or
enable automatic SOAP envelope handling.

The **destinationName** defines the incoming JMS destination to poll. The **connectionFactory** is mandatory in order to connect to a JMS
message broker.

You can simply extend the adapter in a custom class for adding customizations.

```java
@Component
public class MySimulatorJmsAdapter extends SimulatorJmsAdapter {

    @Override
    public String destinationName() {
        return "JMS.Queue.simulator.inbound";
    }
    
    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }
}
```

As you can see the class is annotated with **@Component** annotation. This is because the adapter should be recognized by Spring in order to overwrite the default
JMS adapter behavior. The custom adapter just overwrites the **connectionFactory** and **destinationName** methods so the JMS simulator will connect to the ActiveMQ message broker
and listen for incoming requests on that queue.

This is the simplest way to customize the simulator JMS support. We can also use the adapter extension directly on the Spring boot main application class:

```java
import com.consol.citrus.simulator.annotation.EnableJms;
import com.consol.citrus.simulator.annotation.SimulatorJmsAdapter;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableJms
public class Simulator extends SimulatorJmsAdapter {
                       
    @Override
    public String destinationName() {
        return "/my-rest-service/**";
    }
    
    @Override
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

So we have **@EnableJms** and JMS adapter customizations combined on one single class.