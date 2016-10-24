## Endpoint component configuration

When using generic Citrus endpoints as simulator inbound source we need to configure those endpoint components. The most comfortable way is to
add a **SimulatorEndpointComponentAdapter** implementation to the classpath. The adapter provides several configuration methods.

```java
public abstract class SimulatorEndpointComponentAdapter implements SimulatorEndpointComponentConfigurer {


    @Override
    public abstract Endpoint endpoint(ApplicationContext applicationContext);

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

The adapter defines methods that configure the endpoint component used as inbound source. As usual we can set the mapping key extractor implementation or
add automatic SOAP envelope support.

More importantly we need to define an inbound endpoint that is used as source for scenarios. Let's have a simple endpoint component adapter example.

```java
import com.consol.citrus.simulator.annotation.EnableEndpointComponent;
import com.consol.citrus.simulator.annotation.SimulatorEndpointComponentAdapter;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableEndpointComponent
public class Simulator extends SimulatorEndpointComponentAdapter {
                       
    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }

    @Override
    public Endpoint endpoint(ApplicationContext applicationContext) {
        MailServer mailServer = new MailServer();
        mailServer.setPort(2222);
        mailServer.setAutoStart(true);

        return mailServer;
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        XPathPayloadMappingKeyExtractor mappingKeyExtractor = new XPathPayloadMappingKeyExtractor();
        NamespaceContextBuilder namespaceContextBuilder = new NamespaceContextBuilder();
        namespaceContextBuilder.getNamespaceMappings().put("mail", "http://www.citrusframework.org/schema/mail/message");
        mappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
        mappingKeyExtractor.setXpathExpression("/mail:mail-message/mail:subject");
        return mappingKeyExtractor;
    }
}
```

The custom adapter defines a Citrus mail server endpoint that should be used as inbound source. Any mail message that arrives at this mail server component will
trigger a new simulator scenario then. Also we overwrite the mapping key extractor implementation so that the mail subject evaluates to the scenario that should be executed.

This configuration would lead us to a mail server that responds to incoming mail messages base on the mail subject. So we can have several simulator
scenarios for different mail messages.

```java
@Scenario("Hello")
public class HelloScenario extends SimulatorEndpointScenario {

    @Override
    protected void configure() {
        scenario()
            .receive()
            .payload("<mail-message xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<from>user@citrusframework.org</from>" +
                        "<to>citrus@citrusframework.org</to>" +
                        "<cc></cc>" +
                        "<bcc></bcc>" +
                        "<subject>Hello</subject>" +
                        "<body>" +
                            "<contentType>text/plain; charset=utf-8</contentType>" +
                            "<content>Say Hello!</content>" +
                        "</body>" +
                    "</mail-message>");

        scenario()
            .send()
            .payload("<mail-response xmlns=\"http://www.citrusframework.org/schema/mail/message\">" +
                        "<code>250</code>" +
                        "<message>OK</message>" +
                    "</mail-response>");
    }
}
```

The scenario implementation above uses the base class **SimulatorEndpointScenario** and listens for mail messages of subject **Hello**. The
mail XML marshalling is automatically done by Citrus. This is the usual way how the Citrus mail component handles mail messages and responses. That means we can use the default Citrus
features in our simulator, too. The scenario sends back a positive mail response to the calling client.

This is how we can use any Citrus endpoint component as simulator inbound source. This gives us the opportunity to support a huge set of message transports and
message types in our simulator applications. Each incoming request on the endpoint component triggers a new simulator scenario.