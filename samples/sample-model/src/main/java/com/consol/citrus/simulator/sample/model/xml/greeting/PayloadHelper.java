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

package com.consol.citrus.simulator.sample.model.xml.greeting;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;

/**
 * @author Martin Maher
 */
public class PayloadHelper {
    public Jaxb2Marshaller getMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(com.consol.citrus.simulator.sample.model.xml.greeting.ObjectFactory.class.getPackage().getName());
        return marshaller;
    }

    public JAXBElement<GreetingType> generateGreetingMessage(String name, GreetingEnumType type) {
        return new ObjectFactory().createGreetingMessage(createGreetingType(name, type));
    }

    public JAXBElement<GreetingType> generateGreetingRequest(String name, GreetingEnumType type) {
        return new ObjectFactory().createGreetingRequest(createGreetingType(name, type));
    }

    public JAXBElement<GreetingType> generateGreetingResponse(String name, GreetingEnumType type) {
        return new ObjectFactory().createGreetingResponse(createGreetingType(name, type));
    }

    private GreetingType createGreetingType(String name, GreetingEnumType type) {
        GreetingType greeting = new GreetingType();
        greeting.setName(name);
        greeting.setType(type);
        return greeting;
    }

}
