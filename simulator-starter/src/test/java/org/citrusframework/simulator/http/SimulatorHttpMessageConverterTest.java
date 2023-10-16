package org.citrusframework.simulator.http;

import org.citrusframework.http.controller.HttpMessageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Christoph Deppisch
 */
class SimulatorHttpMessageConverterTest {

    private SimulatorHttpMessageConverter fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new SimulatorHttpMessageConverter();
    }

    @Test
    void testSimulatorMessageConverter() throws IOException {
        assertFalse(fixture.canRead(Object.class, Object.class, MediaType.ALL));
        assertFalse(fixture.canRead(Object.class, MediaType.ALL));
        assertFalse(fixture.canWrite(Object.class, Object.class, MediaType.ALL));
        assertFalse(fixture.canWrite(Object.class, MediaType.ALL));

        assertEquals(fixture.read(String.class, HttpMessageController.class, new MockHttpInputMessage("Hello".getBytes(StandardCharsets.UTF_8))), "Hello");
    }

    @Test
    void testUnsupportedRead() {
        assertThrows(IllegalStateException.class, () -> fixture.read(Object.class, new MockHttpInputMessage("Hello".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    void testUnsupportedWrite() {
        assertThrows(IllegalStateException.class, () -> fixture.write("Hello", MediaType.TEXT_PLAIN, new MockHttpOutputMessage()));
    }

    @Test
    void testUnsupportedGenericWrite() {
        assertThrows(IllegalStateException.class, () -> fixture.write("Hello", String.class, MediaType.TEXT_PLAIN, new MockHttpOutputMessage()));
    }
}
