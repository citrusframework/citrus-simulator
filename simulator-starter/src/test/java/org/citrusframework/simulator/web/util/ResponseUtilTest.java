package org.citrusframework.simulator.web.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
