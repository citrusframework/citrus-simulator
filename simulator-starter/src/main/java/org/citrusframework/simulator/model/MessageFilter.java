/*
 * Copyright 2006-2017 the original author or authors.
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

package org.citrusframework.simulator.model;

import java.time.Instant;

/**
 * Filter for filtering {@link Message}s
 */
public class MessageFilter {
    private Instant fromDate;
    private Instant toDate;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean directionInbound;
    private Boolean directionOutbound;
    private String containingText;

    /**
     * The headerFilter can contain several key/value pairs of
     * headerNames/headerValues. It must be encoded as follows:<br>
     * <br>
     * <b>p1:v1;p2:v2;p3:v3</b><br>
     * <br>
     * with p_x being the parameter name and v_x being the parameter value to
     * filter.
     */
    private String headerFilter;

    public Instant getFromDate() {
        return fromDate;
    }

    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public Instant getToDate() {
        return toDate;
    }

    public void setToDate(Instant toDate) {
        this.toDate = toDate;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getDirectionInbound() {
        return directionInbound;
    }

    public void setDirectionInbound(Boolean directionInbound) {
        this.directionInbound = directionInbound;
    }

    public Boolean getDirectionOutbound() {
        return directionOutbound;
    }

    public void setDirectionOutbound(Boolean directionOutbound) {
        this.directionOutbound = directionOutbound;
    }

    public String getContainingText() {
        return containingText;
    }

    public void setContainingText(String containingText) {
        this.containingText = containingText;
    }

    public String getHeaderFilter() {
        return headerFilter;
    }

    public void setHeaderFilter(String headerFilter) {
        this.headerFilter = headerFilter;
    }
}
