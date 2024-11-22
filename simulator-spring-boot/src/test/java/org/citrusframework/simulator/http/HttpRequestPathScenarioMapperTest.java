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

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import io.apicurio.datamodels.openapi.models.OasDocument;
import io.apicurio.datamodels.openapi.models.OasOperation;
import io.apicurio.datamodels.openapi.v2.models.Oas20Document;
import io.apicurio.datamodels.openapi.v2.models.Oas20Operation;
import io.apicurio.datamodels.openapi.v3.models.Oas30Document;
import io.apicurio.datamodels.openapi.v3.models.Oas30Operation;
import java.util.Arrays;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.openapi.OpenApiSpecification;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class HttpRequestPathScenarioMapperTest {

    public static final String DEFAULT_SCENARIO = "default";
    public static final String FOO_LIST_SCENARIO = "fooListScenario";
    public static final String FOO_LIST_POST_SCENARIO = "fooListPostScenario";
    public static final String BAR_LIST_SCENARIO = "barListScenario";
    public static final String FOO_SCENARIO = "fooScenario";
    public static final String ISSUE_SCENARIO = "issueScenario";
    public static final String BAR_SCENARIO = "barScenario";
    public static final String FOO_DETAIL_SCENARIO = "fooDetailScenario";
    public static final String BAR_DETAIL_SCENARIO = "barDetailScenario";

    public static final String FOOBAR_GET_SCENARIO = "foobarGetScenario";
    public static final String FOOBAR_POST_SCENARIO = "foobarPostScenario";

    @Mock
    private SimulatorConfigurationProperties simulatorConfigurationMock;

    private HttpRequestPathScenarioMapper fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpRequestPathScenarioMapper();
        fixture.setConfiguration(simulatorConfigurationMock);

        doReturn(DEFAULT_SCENARIO).when(simulatorConfigurationMock).getDefaultScenario();
    }

    @ParameterizedTest
    @ValueSource(strings = {"oas2", "oas3"})
    void testGetMappingKey(String version) {
        OpenApiSpecification openApiSpecificationMock = mock();

        OasDocument oasDocument = null;
        if ("oas2".equals(version)) {
            oasDocument = mock(Oas20Document.class);
        } else if ("oas3".equals(version)) {
            oasDocument = mock(Oas30Document.class);
        } else {
            fail("Unexpected version: "+ version);
        }

        HttpScenario foobarGetScenarioMock = mockHttpScenario("GET", "/issues/foobar", FOOBAR_GET_SCENARIO);
        HttpScenario foobarPostScenarioMock = mockHttpScenario("POST", "/issues/foobar", FOOBAR_POST_SCENARIO);

        fixture.setScenarioList(Arrays.asList(new HttpOperationScenario("/issues/foos",
                FOO_LIST_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),

            new HttpOperationScenario("/issues/foos", FOO_LIST_POST_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.POST), null),
            new HttpOperationScenario("/issues/foo/{id}", FOO_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            new HttpOperationScenario("/issues/foo/detail", FOO_DETAIL_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            new HttpOperationScenario("/issues/bars", BAR_LIST_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            new HttpOperationScenario("/issues/{bar}/{id}", ISSUE_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            new HttpOperationScenario("/issues/bar/{id}", BAR_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            new HttpOperationScenario("/issues/bar/detail", BAR_DETAIL_SCENARIO, openApiSpecificationMock, mockOperation(oasDocument, RequestMethod.GET), null),
            foobarGetScenarioMock,
            foobarPostScenarioMock)
        );

        assertEquals(DEFAULT_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET)));
        assertEquals(DEFAULT_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.POST)));
        assertEquals(DEFAULT_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues")));
        assertEquals(FOO_LIST_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foos")));
        assertEquals(FOO_LIST_POST_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.POST).path("/issues/foos")));
        assertEquals(DEFAULT_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.PUT).path("/issues/foos")));
        assertEquals(BAR_LIST_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bars")));
        assertEquals(DEFAULT_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.DELETE).path("/issues/bars")));
        assertEquals(FOO_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/foo/1")));
        assertEquals(BAR_SCENARIO,
            fixture.getMappingKey(new HttpMessage().method(HttpMethod.GET).path("/issues/bar/1")));
        assertEquals(FOO_DETAIL_SCENARIO,
            fixture.getMappingKey(
                new HttpMessage().method(HttpMethod.GET).path("/issues/foo/detail")));
        assertEquals(BAR_DETAIL_SCENARIO,
            fixture.getMappingKey(
                new HttpMessage().method(HttpMethod.GET).path("/issues/bar/detail")));
        assertEquals(FOOBAR_GET_SCENARIO,
            fixture.getMappingKey(
                new HttpMessage().method(HttpMethod.GET).path("/issues/foobar")));
        assertEquals(FOOBAR_POST_SCENARIO,
            fixture.getMappingKey(
                new HttpMessage().method(HttpMethod.POST).path("/issues/foobar")));
        assertEquals(ISSUE_SCENARIO,
            fixture.getMappingKey(
                new HttpMessage().method(HttpMethod.GET).path("/issues/1/2")));

        fixture.setUseDefaultMapping(false);

        HttpMessage httpGetMessage = new HttpMessage().method(HttpMethod.GET);
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(httpGetMessage));

        HttpMessage httpGetIssuesMessage = new HttpMessage().method(HttpMethod.GET).path("/issues");
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(httpGetIssuesMessage));
    }

    private HttpScenario mockHttpScenario(String method, String path, String scenarioId) {
        HttpScenario httpScenario = mock();
        doReturn(method).when(httpScenario).getMethod();
        doReturn(path).when(httpScenario).getPath();
        doReturn(scenarioId).when(httpScenario).getScenarioId();
        return httpScenario;
    }

    private OasOperation mockOperation(OasDocument oasDocument, RequestMethod requestMethod) {

        OasOperation oasOperationMock = null;
        if (oasDocument instanceof  Oas20Document) {
            oasOperationMock = mock(Oas20Operation.class);
        } else if (oasDocument instanceof  Oas30Document) {
            oasOperationMock = mock(Oas30Operation.class);
        } else {
            fail("Unexpected version document type!");
        }
        doReturn(requestMethod.toString()).when(oasOperationMock).getMethod();
        return oasOperationMock;
    }

}
