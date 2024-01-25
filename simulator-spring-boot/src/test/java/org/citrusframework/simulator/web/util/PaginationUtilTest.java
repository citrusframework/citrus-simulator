/*
 * Copyright 2024 the original author or authors.
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.citrusframework.simulator.web.util.PaginationUtil.createPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ExtendWith({MockitoExtension.class})
class PaginationUtilTest {

    @Test
    void createUnsortedPage() {
        // Prepare test data
        List<String> allObjects = asList("Object1", "Object2", "Object3", "Object4", "Object5");

        Pageable mockPageable = mock(Pageable.class);
        doReturn(2L).when(mockPageable).getOffset();
        doReturn(2).when(mockPageable).getPageSize();
        doReturn(Sort.unsorted()).when(mockPageable).getSort();

        Function<String, Optional<? extends Comparator<String>>> getComparatorMock = mock(Function.class);

        // Call the method to test
        Page<String> page = createPage(allObjects, mockPageable, getComparatorMock);

        // Assertions
        assertEquals(2, page.getContent().size(), "The page content size should be 2");
        assertEquals("Object3", page.getContent().get(0), "First object on the second page");
        assertEquals("Object4", page.getContent().get(1), "Second object on the second page");
        assertEquals(5, page.getTotalElements(), "Total elements should be 5");
        assertEquals(3, page.getTotalPages(), "Total pages should be 3");

        verifyNoInteractions(getComparatorMock);
    }

    @Test
    void createSortedPageWithoutComparator() {
        // Prepare test data
        List<String> itemsMock = mock(List.class);

        Pageable mockPageable = mock(Pageable.class);
        doReturn(2L).when(mockPageable).getOffset();
        doReturn(2).when(mockPageable).getPageSize();

        String sortingProperty = "name";
        doReturn(Sort.by(ASC, sortingProperty)).when(mockPageable).getSort();

        Function<String, Optional<? extends Comparator<String>>> getComparatorMock = mock(Function.class);
        doReturn(empty()).when(getComparatorMock).apply(sortingProperty);

        // Call the method to test
        createPage(itemsMock, mockPageable, getComparatorMock);

        // Assertions
        verify(itemsMock, times(2)).size();
        verify(itemsMock).subList(2, 0);
        verifyNoMoreInteractions(itemsMock);
    }

    @Test
    void createSortedPageWithComparator() {
        // Prepare test data
        List<String> allObjects = spy(asList("Object1", "Object2", "Object3", "Object4", "Object5"));

        Stream<String> streamMock = mock(Stream.class);
        doReturn(streamMock).when(allObjects).stream();

        Pageable mockPageable = mock(Pageable.class);
        doReturn(2L).when(mockPageable).getOffset();
        doReturn(2).when(mockPageable).getPageSize();

        String sortingProperty = "name";
        doReturn(Sort.by(ASC, sortingProperty)).when(mockPageable).getSort();

        Comparator<String> comparatorMock = mock(Comparator.class);
        Function<String, Optional<? extends Comparator<String>>> getComparatorMock = mock(Function.class);
        doReturn(of(comparatorMock)).when(getComparatorMock).apply(sortingProperty);

        doReturn(streamMock).when(streamMock).sorted(comparatorMock);
        doReturn(allObjects).when(streamMock).toList();

        // Call the method to test
        var page = createPage(allObjects, mockPageable, getComparatorMock);

        // Assertions
        assertEquals(2, page.getContent().size(), "The page content size should be 2");
        assertEquals(5, page.getTotalElements(), "Total elements should be 5");
        assertEquals(3, page.getTotalPages(), "Total pages should be 3");
    }

    @Test
    void unpaged() {
        // Prepare test data
        List<String> allObjects = asList("Object1", "Object2", "Object3", "Object4", "Object5");

        Function<String, Optional<? extends Comparator<String>>> getComparatorMock = mock(Function.class);

        // Call the method to test
        Page<String> page = createPage(allObjects, Pageable.unpaged(), getComparatorMock);

        // Assertions
        assertFalse(page.getPageable().isPaged(), "Result contains all objects, not a page");
        assertEquals(allObjects, page.getContent(), "The page should contain all objects at once");
        assertEquals(allObjects.size(), page.getTotalElements(), "Total elements should match available elements");
        assertEquals(1, page.getTotalPages(), "Total pages should be 1");

        verifyNoInteractions(getComparatorMock);
    }
}
