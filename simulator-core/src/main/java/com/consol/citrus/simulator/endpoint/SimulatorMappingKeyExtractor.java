/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.simulator.endpoint;

import com.consol.citrus.TestCase;
import com.consol.citrus.endpoint.adapter.mapping.AbstractMappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.message.Message;
import com.consol.citrus.simulator.correlation.CorrelationHandler;
import com.consol.citrus.simulator.listener.SimulatorActiveTestListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Christoph Deppisch
 */
public class SimulatorMappingKeyExtractor extends AbstractMappingKeyExtractor {

    @Autowired
    private SimulatorActiveTestListener activeTestListener;

    /**
     * Delegate mapping key extractor
     */
    private MappingKeyExtractor delegate;

    /**
     * Intermediate message handler mapping
     */
    public static final String INTERVENING_MESSAGE_MAPPING = "INTERVENING_MESSAGE";

    @Override
    protected String getMappingKey(Message request) {
        for (TestCase activeTest : activeTestListener.getActiveTests()) {
            if (activeTest.getVariableDefinitions().containsKey(CorrelationHandler.class.getName()) &&
                    ((CorrelationHandler) activeTest.getVariableDefinitions().get(CorrelationHandler.class.getName())).isHandlerFor(request)) {
                return INTERVENING_MESSAGE_MAPPING;
            }
        }

        return delegate.extractMappingKey(request);
    }

    /**
     * Sets the delegate property.
     *
     * @param delegate
     */
    public void setDelegate(MappingKeyExtractor delegate) {
        this.delegate = delegate;
    }
}
