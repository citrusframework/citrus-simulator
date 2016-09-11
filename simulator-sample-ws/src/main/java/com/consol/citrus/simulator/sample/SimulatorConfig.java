package com.consol.citrus.simulator.sample;

import com.consol.citrus.simulator.annotation.EnableWebService;
import com.consol.citrus.simulator.annotation.SimulatorWebServiceAdapter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christoph Deppisch
 */
@Configuration
@EnableWebService
@ComponentScan
public class SimulatorConfig extends SimulatorWebServiceAdapter {

}
