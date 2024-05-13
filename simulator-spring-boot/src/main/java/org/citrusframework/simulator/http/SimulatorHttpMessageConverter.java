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

package org.citrusframework.simulator.http;

import org.citrusframework.http.controller.HttpMessageController;
import org.citrusframework.http.message.DelegatingHttpEntityMessageConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Special generic message converter only applies to {@link HttpMessageController} context class when reading data.
 * If context class is different than that default Spring internal message converters should apply. This makes sure that all
 * simulator related requests are handled by the {@link DelegatingHttpEntityMessageConverter} for future processing in Citrus.
 *
 * All other requests should be converted by conventional Spring converters for normal MVC processing.
 *
 * @author Christoph Deppisch
 */
@SuppressWarnings("NullableProblems")
public class SimulatorHttpMessageConverter implements GenericHttpMessageConverter<Object> {

    private final DelegatingHttpEntityMessageConverter delegate = new DelegatingHttpEntityMessageConverter();

    @Override
    public boolean canRead(Type type, Class contextClass, MediaType mediaType) {
        return HttpMessageController.class.equals(contextClass);
    }

    @Override
    public Object read(Type type, Class contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return delegate.read(Object.class, inputMessage);
    }

    @Override
    public boolean canWrite(Type type, Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        throw new IllegalStateException("Illegal write operation on simulator message converter.");
    }

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return delegate.getSupportedMediaTypes();
    }

    @Override
    public Object read(Class clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        throw new IllegalStateException("Illegal read operation on simulator message converter.");
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        throw new IllegalStateException("Illegal write operation on simulator message converter.");
    }
}
