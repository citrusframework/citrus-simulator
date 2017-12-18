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

package com.consol.citrus.simulator.sample.service;

import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.simulator.sample.model.QueryParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.consol.citrus.simulator.sample.model.QueryParameter.*;

@Service
public class QueryParameterService {

    public String getIban(String queryParameters) {
        return extractQueryParameterValue(queryParameters, IBAN).orElseThrow(() -> new SimulatorException("missing query parameter: " + IBAN));
    }

    public String getSortCode(String queryParameters) {
        return extractQueryParameterValue(queryParameters, SORT_CODE).orElseThrow(() -> new SimulatorException("missing query parameter: " + SORT_CODE));
    }

    public String getBankAccountNumber(String queryParameters) {
        return extractQueryParameterValue(queryParameters, ACCOUNT_NUMBER).orElseThrow(() -> new SimulatorException("missing query parameter: " + ACCOUNT_NUMBER));
    }

    private Optional<String> extractQueryParameterValue(String queryParameters, String name) {
        if (!StringUtils.hasLength(name)) {
            throw new IllegalArgumentException("invalid invocation: 'name' cannot be null or empty");
        }

        return toQueryParameters(queryParameters).stream()
                .filter(queryParameter -> name.equals(queryParameter.getName()))
                .map(QueryParameter::getValue)
                .findFirst();
    }

    private List<QueryParameter> toQueryParameters(String queryParameterString) {
        if (!StringUtils.hasLength(queryParameterString)) {
            return Collections.emptyList();
        }

        final List<QueryParameter> queryParameters = new ArrayList<>();
        final String[] queryParams = StringUtils.tokenizeToStringArray(queryParameterString, ",");
        for (String queryParam : queryParams) {
            final String[] paramKeyValue = StringUtils.tokenizeToStringArray(queryParam, "=");
            if (paramKeyValue.length == 2) {
                queryParameters.add(QueryParameter.builder().name(paramKeyValue[0]).value(paramKeyValue[1]).build());
            }
        }
        return Collections.unmodifiableList(queryParameters);
    }
}
