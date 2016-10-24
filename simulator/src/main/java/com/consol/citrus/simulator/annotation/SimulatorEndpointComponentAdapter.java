package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorEndpointComponentAdapter implements SimulatorEndpointComponentConfigurer {

    @Override
    public boolean useSoapEnvelope() {
        return false;
    }

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new XPathPayloadMappingKeyExtractor();
    }
}
