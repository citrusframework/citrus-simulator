package org.citrusframework.simulator.http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.consol.citrus.http.controller.HttpMessageController;
import com.consol.citrus.http.message.DelegatingHttpEntityMessageConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

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
    public Object read(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new IllegalStateException("Illegal read operation on simulator message converter.");
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        throw new IllegalStateException("Illegal write operation on simulator message converter.");
    }
}
