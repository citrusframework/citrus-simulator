package com.consol.citrus.simulator.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SimulatorEndpointComponentSupport.class)
public @interface EnableEndpointComponent {
}
