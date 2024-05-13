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

package org.citrusframework.simulator.web.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseUtilTest {

    @Test
    void wrapOrNotFoundWithPresentOptionalShouldReturnResponseEntityWithOkStatus() {
        String expectedBody = "test";
        ResponseEntity<String> response = ResponseUtil.wrapOrNotFound(Optional.of(expectedBody));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void wrapOrNotFoundWithEmptyOptionalShouldThrowResponseStatusException() {
        var maybeResponse = Optional.empty();
        ResponseStatusException responseStatusException = assertThrows(
            ResponseStatusException.class,
            () -> ResponseUtil.wrapOrNotFound(maybeResponse)
        );
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
    }

    @Test
    void wrapOrNotFoundWithPresentOptionalAndHeadersShouldReturnResponseEntityWithOkStatusAndHeaders() {
        String headerKey = "Test-Header";
        String headerValue = "HeaderValue";

        String expectedBody = "test";

        HttpHeaders headers = new HttpHeaders();
        headers.add(headerKey, headerValue);

        ResponseEntity<String> response = ResponseUtil.wrapOrNotFound(Optional.of(expectedBody), headers);
        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());

        HttpHeaders responseHeaders = response.getHeaders();
        assertTrue(responseHeaders.containsKey(headerKey));

        List<String> testHeaders = responseHeaders.get(headerKey);
        assertNotNull(testHeaders);
        assertEquals(1, testHeaders.size());
        assertEquals(headerValue, testHeaders.get(0));
    }

    @Test
    void wrapOrNotFoundWithEmptyOptionalAndHeadersShouldThrowResponseStatusException() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Test-Header", "HeaderValue");

        var maybeResponse = Optional.empty();
        ResponseStatusException responseStatusException = assertThrows(
            ResponseStatusException.class,
            () -> ResponseUtil.wrapOrNotFound(maybeResponse, headers)
        );
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());

        assertEquals(0, responseStatusException.getHeaders().size());
    }
}
