package com.consol.citrus.simulator.annotation;

import org.springframework.context.annotation.Import;

/**
 * @author Christoph Deppisch
 */
@Import(SimulatorJmsSupport.class)
public @interface EnableJms {
}
