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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.stereotype.Component;


/**
 * Aspect for adding additional behavior to Endpoints. This is used in particular for intercepting the creation of
 * {@link com.consol.citrus.messaging.Consumer}s and {@link com.consol.citrus.messaging.Producer}s.
 */
@Aspect
@Component
public class EndpointAspect {
    private final EndpointConsumerInterceptor consumerInterceptor;
    private final EndpointProducerInterceptor producerInterceptor;

    public EndpointAspect(EndpointConsumerInterceptor consumerInterceptor, EndpointProducerInterceptor producerInterceptor) {
        this.consumerInterceptor = consumerInterceptor;
        this.producerInterceptor = producerInterceptor;
    }

    @Around("execution(* com.consol.citrus.endpoint.Endpoint.createConsumer(..))")
    public Object wrapConsumer(ProceedingJoinPoint joinPoint) throws Throwable {
        ProxyFactory proxyFactory = new ProxyFactory(joinPoint.proceed());
        proxyFactory.addAdvice(consumerInterceptor);
        return proxyFactory.getProxy();
    }

    @Around("execution(* com.consol.citrus.endpoint.Endpoint.createProducer(..))")
    public Object wrapProducer(ProceedingJoinPoint joinPoint) throws Throwable {
        ProxyFactory proxyFactory = new ProxyFactory(joinPoint.proceed());
        proxyFactory.addAdvice(producerInterceptor);
        return proxyFactory.getProxy();
    }
}
