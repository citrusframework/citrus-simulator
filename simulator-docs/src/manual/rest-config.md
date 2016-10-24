## REST configuration

Once the REST support is enabled on the simulator we have different configuration options. The most comfortable way is to
add a **SimulatorRestAdapter** implementation to the classpath. The adapter provides several configuration methods.

```java
public abstract class SimulatorRestAdapter implements SimulatorRestConfigurer {

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new AnnotationRequestMappingKeyExtractor();
    }

    @Override
    public HandlerInterceptor[] interceptors() {
        return new HandlerInterceptor[] { new LoggingHandlerInterceptor() };
    }

    @Override
    public String urlMapping() {
        return "/services/rest/**";
    }
}
```   

The adapter defines methods that configure the simulator REST handling. For instance we can add another mapping key extractor implementation or
add handler interceptors to the REST API call handling.

The **urlMapping** defines how clients can access the simulator REST API. Assuming the Spring boot simulator application is running on port 8080 the
REST API would be accessible on this URI:

```
http://localhhost:8080/services/rest/*
```

The clients can send GET, POST, DELETE and other calls to that endpoint URI then. The simulator will respond with respective responses based on the called
scenario.

You can simply extend the adapter in a custom class for adding customizations.

```java
@Component
public class MySimulatorRestAdapter extends SimulatorRestAdapter {

    @Override
    public String urlMapping() {
        return "/my-rest-service/**";
    }
}
```

As you can see the class is annotated with **@Component** annotation. This is because the adapter should be recognized by Spring in order to overwrite the default
REST adapter behavior. The custom adapter just overwrites the **urlMapping** method so the REST simulator API will be accessible for clients under this endpoint URI:

```
http://localhhost:8080/my-rest-service/*
```

This is the simplest way to customize the simulator REST support. We can also use the adapter extension directly on the Spring boot main application class:

```java
import com.consol.citrus.simulator.annotation.EnableRest;
import com.consol.citrus.simulator.annotation.SimulatorRestAdapter;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
@EnableRest
public class Simulator extends SimulatorRestAdapter {
                       
    @Override
    public String urlMapping() {
        return "/my-rest-service/**";
    }
    
    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        HeaderMappingKeyExtractor mappingKeyExtractor = new HeaderMappingKeyExtractor();
        mappingKeyExtractor.setHeaderName("X-simulator-scenario");
        return mappingKeyExtractor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

So we have **@EnableRest** and REST adapter customizations combined on one single class.

### Advanced customizations

For a more advanced configuration option we can extend the **SimulatorRestSupport** implementation.

```java
import com.consol.citrus.simulator.annotation.EnableRest;
import com.consol.citrus.simulator.annotation.SimulatorRestSupport;
import com.consol.citrus.simulator.annotation.SimulatorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SimulatorApplication
public class Simulator extends SimulatorRestSupport {
                       
    @Override
    protected String getUrlMapping() {
        return "/my-rest-service/**";
    }
    
    @Override
    public FilterRegistrationBean requestCachingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new RequestCachingServletFilter());

        String urlMapping = getUrlMapping();
        if (urlMapping.endsWith("**")) {
            urlMapping = urlMapping.substring(0, urlMapping.length() - 1);
        }
        filterRegistrationBean.setUrlPatterns(Collections.singleton(urlMapping));
        return filterRegistrationBean;
    }
    
    @Override
    public HandlerMapping handlerMapping(ApplicationContext applicationContext) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        handlerMapping.setAlwaysUseFullPath(true);

        Map<String, Object> mappings = new HashMap<>();
        mappings.put(getUrlMapping(), getRestController(applicationContext));

        handlerMapping.setUrlMap(mappings);
        handlerMapping.setInterceptors(interceptors());

        return handlerMapping;
    }

    public static void main(String[] args) {
        SpringApplication.run(Simulator.class, args);
    }
}
```

With that configuration option we can overwrite REST support auto configuration features on the simulator such as the **requestCachingFilter** or the **handlerMapping**. 
We can not use the **@EnableRest** auto configuration annotation then. Instead we extend the **SimulatorRestSupport** implementation directly.