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

package org.citrusframework.simulator.ws;

import org.springframework.ws.client.support.interceptor.ClientInterceptor;

/**
 * @author Martin Maher
 */
public interface SimulatorWebServiceClientConfigurer {
    /**
     * Gets the request url. This is where the SOAP client sends the requests to.
     *
     * @return
     */
    String requestUrl();

    /**
     * Gets the list of client interceptors.
     *
     * @return
     */
    ClientInterceptor[] interceptors();
}
