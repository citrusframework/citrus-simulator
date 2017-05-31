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

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.http.interceptor.LoggingHandlerInterceptor;
import com.consol.citrus.simulator.http.AnnotationRequestMappingKeyExtractor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Christoph Deppisch
 */
public abstract class SimulatorRestAdapter implements SimulatorRestConfigurer {

    @Override
    public MappingKeyExtractor mappingKeyExtractor() {
        return new AnnotationRequestMappingKeyExtractor();
    }

    @Override
    public HandlerInterceptor[] interceptors() {
        return new HandlerInterceptor[]{new LoggingHandlerInterceptor()};
    }

    @Override
    public String urlMapping() {
        return "/services/rest/**";
    }
}
