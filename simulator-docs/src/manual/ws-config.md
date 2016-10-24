## Web Service configuration

Once the SOAP support is enabled on the simulator we have different configuration options. The most comfortable way is to
add a **SimulatorWebServiceAdapter** implementation to the classpath. The adapter provides several configuration methods.

```java
public abstract class SimulatorWebServiceAdapter implements SimulatorWebServiceConfigurer {
    @Override
    public String servletMapping() {
        return "/services/ws/*";
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }

    @Override
    public EndpointInterceptor[] interceptors() {
        return new EndpointInterceptor[] { new LoggingEndpointInterceptor() };
    }
}
```   

The adapter defines methods that configure the simulator SOAP message handling. For instance we can add another mapping key extractor implementation or
add endpoint interceptors to the SOAP service call handling.

The **servletMapping** defines how clients can access the simulator SOAP service. Assuming the Spring boot simulator application is running on port 8080 the
SOAP service would be accessible on this URI:

```
http://localhhost:8080/services/ws/*
```

The clients can send SOAP calls to that endpoint URI then. The simulator will respond with respective SOAP responses based on the called
scenario.

You can simply extend the adapter in a custom class for adding customizations.

```java
@Component
public class MySimulatorWebServiceAdapter extends SimulatorWebServiceAdapter {

    @Override
    public String servletMapping() {
        return "/my-soap-service/**";
    }
}
```

As you can see the class is annotated with **@Component** annotation. This is because the adapter should be recognized by Spring in order to overwrite the default
SOAP adapter behavior. The custom adapter just overwrites the **servletMapping** method so the SOAP simulator API will be accessible for clients under this endpoint URI:

```
http://localhhost:8080/my-soap-service/*
```

This is the simplest way to customize the simulator SOAP support. We can also use the adapter extension directly on the Spring boot main application class:

```java
import com.consol.citrus.simulator.annotation.EnableWebService;
import com.consol.citrus.simulator.annotation.SimulatorWebServiceAdapter;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableWebService
public class Simulator extends SimulatorWebServiceAdapter {
                       
    @Override
    public String servletMapping() {
        return "/my-soap-service/**";
    }
    
    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new SoapActionMappingKeyExtractor();
    }

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

So we have **@EnableWebService** and SOAP adapter customizations combined on one single class.

### Advanced customizations

For a more advanced configuration option we can extend the **SimulatorWebServiceSupport** implementation.

```java
import com.consol.citrus.simulator.annotation.EnableWebService;
import com.consol.citrus.simulator.annotation.SimulatorWebServiceSupport;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
public class Simulator extends SimulatorWebServiceSupport {
                       
    @Override
    protected String getServletMapping() {
        return "/my-soap-service/**";
    }
    
    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, getDispatcherServletMapping());
    }

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

With that configuration option we can overwrite SOAP support auto configuration features on the simulator such as the **messageDispatcherServlet**. 
We can not use the **@EnableWebService** auto configuration annotation then. Instead we extend the **SimulatorWebServiceSupport** implementation directly.