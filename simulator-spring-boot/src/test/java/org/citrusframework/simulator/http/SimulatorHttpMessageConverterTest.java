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
