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

package com.consol.citrus.simulator.model;

import java.util.Date;
import lombok.Data;

/**
 * Filter for filtering {@link Message}s
 */
@Data
public class MessageFilter {
    private Date fromDate;
    private Date toDate;
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

}
