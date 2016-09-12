package com.consol.citrus.simulator.annotation;

import org.springframework.context.annotation.Import;

/**
 * @author Christoph Deppisch
 */
@Import(SimulatorWebServiceSupport.class)
public @interface EnableWebService {
}
