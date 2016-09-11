package com.consol.citrus.simulator.config;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
public class SimulatorImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (StringUtils.hasText(SimulatorConfiguration.SIMULATOR_CONFIGURATION_CLASS)) {
            return new String[] { SimulatorConfiguration.SIMULATOR_CONFIGURATION_CLASS };
        } else if (ClassUtils.isPresent("com.consol.citrus.simulator.SimulatorConfig", this.getClass().getClassLoader())) {
            return new String[] { "com.consol.citrus.simulator.SimulatorConfig" };
        } else {
            return new String[] {};
        }
    }
}
