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
package com.consol.citrus.simulator.http;

import com.consol.citrus.http.message.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks whether the {@link HttpMessage} satisfies the supported {@link RequestMapping} definition.
 */
public class HttpRequestAnnotationMatcher {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestAnnotationMatcher.class);

    /** Request path matcher */
    private PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Prevent instantiation other that using factory method
     */
    private HttpRequestAnnotationMatcher() {
        super();
    }

    /**
     * Factory method creating new instance.
     * @return
     */
    public static HttpRequestAnnotationMatcher instance() {
        return new HttpRequestAnnotationMatcher();
    }

    /**
     * In the event that the {@code requestMapping} contains one or more supported paths, the path from the HTTP
     * {@code request} is compared against the list of supported paths.
     *
     * @param request        the http request
     * @param requestMapping the request mapping containing the supported methods
     * @param exactMatch     flag to indicate exact request path match, if true only exact matches are allowed, if false also wildcards and path variables are allowed
     * @return
     */
    public boolean checkRequestPathSupported(HttpMessage request, RequestMapping requestMapping, boolean exactMatch) {
        final String requestPath = Optional.ofNullable(request.getPath()).orElse("");
        final String[] supportedRequestPaths = requestMapping.value();
        if (supportedRequestPaths.length > 0) {
            for (String supportedRequestPath : supportedRequestPaths) {
                if (exactMatch ? supportedRequestPath.equals(requestPath) : pathMatcher.match(supportedRequestPath, requestPath)) {
                    LOG.debug("Request path {} supported. Path found in the list of supported request paths: {}",
                            requestPath, supportedRequestPaths);
                    return true;
                }
            }
            LOG.debug("Request path {} not supported. Path not found in the list of supported request paths: {}",
                    requestPath, supportedRequestPaths);
            return false;
        }

        LOG.debug("Request path {} supported. All request paths are supported", requestPath);
        return true;
    }

    /**
     * In the event that the {@code requestMapping} contains one or more supported methods, the method from the HTTP
     * {@code request} is compared against the list of supported methods.
     *
     * @param request        the http request
     * @param requestMapping the request mapping containing the supported methods
     * @return
     */
    public boolean checkRequestMethodSupported(HttpMessage request, RequestMapping requestMapping) {
        final RequestMethod[] requestMethods = requestMapping.method();
        final String actualRequestMethod = request.getRequestMethod() != null ? request.getRequestMethod().name() : HttpMethod.POST.name();
        if (requestMethods.length > 0) {
            for (RequestMethod method : requestMethods) {
                if (method.name().equals(actualRequestMethod)) {
                    LOG.debug("Request method {} supported. Found in the list of supported request methods: {}",
                            actualRequestMethod, requestMethods);
                    return true;
                }
            }
            LOG.debug("Request method {} not supported. No contained in the list of supported request methods: {}",
                    actualRequestMethod, requestMethods);
            return false;
        }

        LOG.debug("Request method {} supported. All request methods are supported", actualRequestMethod);
        return true;
    }

    /**
     * Compares the list of HTTP {@code request} query parameters against the list of supported
     * {@code requestMapping} query parameters.
     * <br> For example:
     * <br> Expected: ["a", "b", "!c", "!d"], Provided ["a=1", "d=4", "e=5"]
     * <br> => Result: ["b", "d"] (invalid)
     * <br> Expected: ["a", "b", "!c", "!d"], Provided ["e=5"]
     * <br> => Result: ["a","b"] (invalid)
     * <br> Expected: ["a", "b", "!c", "!d"], Provided ["a=1", "b=2", "e=5"]
     * <br> => Result: [] (valid)
     * <br>
     *
     * @param request        the http request
     * @param requestMapping the request mapping containing the supported query parameters
     * @return Returns true if the request satisfies the supported query parameters, otherwise false
     */
    public boolean checkRequestQueryParamsSupported(HttpMessage request, RequestMapping requestMapping) {
        final List<String> annotatedQueryParams = getAnnotatedQueryParams(requestMapping);
        final List<String> requestQueryParams = getRequestQueryParams(request);

        final List<String> invalidQueryParams = getInvalidQueryParams(requestQueryParams, annotatedQueryParams);
        if (!invalidQueryParams.isEmpty()) {
            LOG.debug("Request query parameters '{}' do not match the supported query parameters: {} (invalid: {})",
                    requestQueryParams, annotatedQueryParams, invalidQueryParams);
        } else {
            LOG.debug("Request query parameters '{}' match the supported query parameters: {}",
                    requestQueryParams, annotatedQueryParams);
        }
        return invalidQueryParams.isEmpty();
    }

    private List<String> getInvalidQueryParams(List<String> requestQueryParams, List<String> annotatedQueryParams) {
        final List<String> requestQueryParamKeys = getQueryParamKeys(requestQueryParams);
        final List<String> invalidRequestQueryParamKeys = new ArrayList<>();

        final String cannotContainKeyPrefix = "!";

        invalidRequestQueryParamKeys.addAll(annotatedQueryParams.stream()
                .filter(k -> !k.startsWith(cannotContainKeyPrefix))
                .filter(k -> !requestQueryParamKeys.contains(k))
                .map(k -> String.format("Expected but missing: %s", k))
                .collect(Collectors.toList()));

        invalidRequestQueryParamKeys.addAll(annotatedQueryParams.stream()
                .filter(k -> k.startsWith(cannotContainKeyPrefix))
                .filter(k -> requestQueryParamKeys.contains(k.substring(cannotContainKeyPrefix.length())))
                .map(k -> String.format("Unexpected but present: %s", k.substring(cannotContainKeyPrefix.length())))
                .collect(Collectors.toList()));

        return invalidRequestQueryParamKeys;
    }

    /**
     * Returns the list of annotated query parameters.
     *
     * @param requestMapping the request mapping annotation
     * @return the list of query parameters or an empty list
     */
    private List<String> getAnnotatedQueryParams(RequestMapping requestMapping) {
        List<String> annotatedQueryParams = new ArrayList<>();
        if (requestMapping != null) {
            annotatedQueryParams.addAll(Arrays.asList(requestMapping.params()));
        }
        return annotatedQueryParams;
    }

    /**
     * Returns the list of query parameters from the request.
     *
     * @param request the http request
     * @return the list of query parameters or an empty list
     */
    private List<String> getRequestQueryParams(HttpMessage request) {
        final List<String> queryParams = new ArrayList<>();
        final String queryParamsStr = request.getQueryParams();
        if (StringUtils.hasLength(queryParamsStr)) {
            final String[] tokenizedQueryParams = StringUtils.tokenizeToStringArray(queryParamsStr,
                    ",",
                    false,
                    false);
            queryParams.addAll(Arrays.asList(tokenizedQueryParams));

        }
        return queryParams;
    }

    private List<String> getQueryParamKeys(List<String> queryParams) {
        return queryParams.stream()
                .map(queryParam -> getQueryParamKeyValue(queryParam)[0])
                .collect(Collectors.toList());
    }

    private String[] getQueryParamKeyValue(String queryParam) {
        if (queryParam.contains("=")) {
            return StringUtils.tokenizeToStringArray(queryParam, "=");
        }
        return new String[]{"", ""};
    }
}
