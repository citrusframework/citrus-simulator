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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.HttpHeaders.LINK;
import static org.yaml.snakeyaml.util.UriEncoder.encode;

/**
 * Utility class for handling pagination.
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">GitHub API</a>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 */
public interface PaginationUtil {

    String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

    /**
     * Creates a paginated {@link Page} of items based on the given {@link Pageable} and a custom comparator function.
     * <p>
     * If sorting is specified in the {@link Pageable}, the items are sorted using the provided comparator function
     * before pagination.
     * The original {@link List} won't be touched in any way.
     *
     * @param items         The list of items to be paginated.
     * @param pageable      The {@link Pageable} object containing pagination and sorting information.
     * @param getComparator A function to obtain a {@link Comparator} based on the property name.
     * @param <T>           The type of objects in the list.
     * @return A paginated {@link Page} of items.
     */
    static <T> Page<T> createPage(List<T> items, Pageable pageable, Function<String, Optional<? extends Comparator<T>>> getComparator) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(items);
        }

        final AtomicReference<List<T>> itemsReference = new AtomicReference<>(items);
        if (pageable.getSort().isSorted()) {
            createComparatorForSort(pageable.getSort(), getComparator)
                .ifPresent(comparator -> itemsReference.set(items.stream().sorted(comparator).toList()));
        }

        var sortedItems = itemsReference.get();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedItems.size());

        var pageContent = sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    /**
     * Creates a composite {@link Comparator} for sorting based on the specified {@link Sort} object.
     * <p>
     * This method constructs a comparator by combining individual property comparators.
     * It handles sorting direction (ascending or descending) as specified in the sort order.
     *
     * @param sort          The {@link Sort} object specifying the sorting criteria.
     * @param getComparator A function to obtain a {@link Comparator} based on the property name.
     * @param <T>           The type of objects to be compared.
     * @return An {@link Optional} containing the composite {@link Comparator}, or empty if no valid comparator could be created.
     */
    private static <T> Optional<Comparator<T>> createComparatorForSort(Sort sort, Function<String, Optional<? extends Comparator<T>>> getComparator) {
        final AtomicReference<Comparator<T>> comparatorRef = new AtomicReference<>();

        for (Sort.Order order : sort) {
            getComparator.apply(order.getProperty())
                .map(orderComparator -> order.getDirection().equals(ASC) ? orderComparator : orderComparator.reversed())
                .ifPresent(newComparator -> comparatorRef.updateAndGet(existingComparator -> {
                    if (isNull(existingComparator)) {
                        return newComparator;
                    } else {
                        return existingComparator.thenComparing(newComparator);
                    }
                }));
        }

        return Optional.ofNullable(comparatorRef.get());
    }

    /**
     * Generate pagination headers for a Spring Data {@link org.springframework.data.domain.Page} object.
     *
     * @param uriBuilder The URI builder.
     * @param page       The page.
     * @param <T>        The type of object.
     * @return http header.
     */
    static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        var headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));

        int pageNumber = page.getNumber();
        int pageSize = page.getSize();

        var link = new StringBuilder();

        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next"))
                .append(",");
        }

        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev"))
                .append(",");
        }

        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
            .append(",")
            .append(prepareLink(uriBuilder, 0, pageSize, "first"));

        headers.add(LINK, link.toString());

        return headers;
    }

    /**
     * Prepares a paginated link with the specified page number and page size.
     * <p>
     * The link is formatted as per RFC 5988 (Web Linking) and is used in the 'Link' HTTP header.
     *
     * @param uriBuilder The {@link UriComponentsBuilder} to build the URI.
     * @param pageNumber The page number for the link.
     * @param pageSize   The size of the page.
     * @param relType    The relation type of the link (e.g., "next", "prev").
     * @return A formatted link string for pagination.
     */
    private static String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format(HEADER_LINK_FORMAT, preparePageUri(uriBuilder, pageNumber, pageSize), relType);
    }

    /**
     * Prepares a URI for pagination by replacing query parameters related to page number and size.
     * <p>
     * This method ensures that the URI correctly represents the state of pagination.
     *
     * @param uriBuilder The {@link UriComponentsBuilder} to build the URI.
     * @param pageNumber The page number for the URI.
     * @param pageSize   The size of the page.
     * @return A string representation of the URI with updated pagination query parameters.
     */
    private static String preparePageUri(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize) {
        return uriBuilder.replaceQueryParam("page", Integer.toString(pageNumber))
            .replaceQueryParam("size", Integer.toString(pageSize))
            .toUriString()
            .replace(",", encode(","))
            .replace(";", encode(";"));
    }
}
