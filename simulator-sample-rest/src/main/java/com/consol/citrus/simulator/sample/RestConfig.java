package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.EnableRest;
import com.consol.citrus.simulator.annotation.SimulatorRestAdapter;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christoph Deppisch
 */
@Configuration
@EnableRest
public class RestConfig extends SimulatorRestAdapter {

}
