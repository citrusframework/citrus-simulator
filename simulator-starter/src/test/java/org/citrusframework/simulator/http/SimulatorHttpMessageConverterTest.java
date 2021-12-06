package org.citrusframework.simulator.http;

import java.io.IOException;
import java.nio.charset.Charset;

import com.consol.citrus.http.controller.HttpMessageController;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class SimulatorHttpMessageConverterTest {

    private SimulatorHttpMessageConverter converter = new SimulatorHttpMessageConverter();

    @Test
    public void testSimulatorMessageConverter() throws IOException {
        Assert.assertFalse(converter.canRead(Object.class, Object.class, MediaType.ALL));
        Assert.assertFalse(converter.canRead(Object.class, MediaType.ALL));
        Assert.assertFalse(converter.canWrite(Object.class, Object.class, MediaType.ALL));
        Assert.assertFalse(converter.canWrite(Object.class, MediaType.ALL));

        Assert.assertEquals(converter.read(String.class, HttpMessageController.class, new MockHttpInputMessage("Hello".getBytes(Charset.forName("UTF-8")))), "Hello");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnsupportedRead() throws IOException {
        converter.read(Object.class, new MockHttpInputMessage("Hello".getBytes(Charset.forName("UTF-8"))));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnsupportedWrite() {
        converter.write("Hello", MediaType.TEXT_PLAIN, new MockHttpOutputMessage());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnsupportedGenericWrite() {
        converter.write("Hello", String.class, MediaType.TEXT_PLAIN, new MockHttpOutputMessage());
    }

}
