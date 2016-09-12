package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.EnableRest;
import com.consol.citrus.simulator.annotation.SimulatorRestAdapter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christoph Deppisch
 */
@Configuration
@EnableRest
@ComponentScan
public class SimulatorConfig extends SimulatorRestAdapter {

}
