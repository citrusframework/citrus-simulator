package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;

/**
 * @author Christoph Deppisch
 */
public interface SimulatorRestConfigurer {

    /**
     * Gets the mapping key extractor.
     * @return
     */
    MappingKeyExtractor mappingKeyExtractor();
}
