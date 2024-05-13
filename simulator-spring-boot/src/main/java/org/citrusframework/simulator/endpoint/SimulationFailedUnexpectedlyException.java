/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.endpoint;

import org.citrusframework.message.DefaultMessage;

/**
 * An implementation of a {@link org.citrusframework.message.Message} that hints that a simulation has failed. Because
 * of the nature of {@link java.util.concurrent.CompletableFuture<org.citrusframework.message.Message>}'s, there is no
 * other way than mapping the response message in case of an error. Exception-propagation from the asynchronous thread
 * back into the parent does not exist.
 *
 * @see SimulatorEndpointAdapter
 */
public class SimulationFailedUnexpectedlyException extends DefaultMessage {

    public static final String EXCEPTION_TYPE = SimulationFailedUnexpectedlyException.class.getSimpleName() + ":Exception";

    public SimulationFailedUnexpectedlyException(Throwable e) {
        super(e);
    }

    @Override
    public String getType() {
        return EXCEPTION_TYPE;
    }
}
