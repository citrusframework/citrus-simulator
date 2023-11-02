package org.citrusframework.simulator.web.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PaginationUtilTest {

    @Test
    void createPage() {
        // Prepare test data
        List<String> allObjects = Arrays.asList("Object1", "Object2", "Object3", "Object4", "Object5");
        Pageable pageable = PageRequest.of(1, 2); // Requesting the second page with a size of 2

        Pageable mockPageable = mock(Pageable.class);
        doReturn(2L).when(mockPageable).getOffset();
        doReturn(2).when(mockPageable).getPageSize();
        doReturn(pageable).when(mockPageable).previousOrFirst();

        // Call the method to test
        Page<String> page = PaginationUtil.createPage(allObjects, mockPageable);

        // Assertions
        assertEquals(2, page.getContent().size(), "The page content size should be 2");
        assertEquals("Object3", page.getContent().get(0), "First object on the second page");
        assertEquals("Object4", page.getContent().get(1), "Second object on the second page");
        assertEquals(5, page.getTotalElements(), "Total elements should be 5");
        assertEquals(3, page.getTotalPages(), "Total pages should be 3");
    }

    @Test
    void unpaged() {
        // Prepare test data
        List<String> allObjects = Arrays.asList("Object1", "Object2", "Object3", "Object4", "Object5");

        // Call the method to test
        Page<String> page = PaginationUtil.createPage(allObjects, Pageable.unpaged());

        // Assertions
        assertFalse(page.getPageable().isPaged(), "Result contains all objects, not a page");
        assertEquals(allObjects, page.getContent(), "The page should contain all objects at once");
        assertEquals(allObjects.size(), page.getTotalElements(), "Total elements should match available elements");
        assertEquals(1, page.getTotalPages(), "Total pages should be 1");
    }
}
