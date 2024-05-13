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

import jakarta.annotation.Nonnull;
import org.citrusframework.exceptions.CitrusRuntimeException;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@ExtendWith(MockitoExtension.class)
class HttpRequestAnnotationScenarioMapperTest {

    @Mock
    private SimulatorConfigurationProperties simulatorConfiguration;

    private HttpRequestAnnotationScenarioMapper fixture;

    @BeforeEach
    void beforeEachSetup() {
        fixture = new HttpRequestAnnotationScenarioMapper();
        fixture.setConfiguration(simulatorConfiguration);

        doReturn("default").when(simulatorConfiguration).getDefaultScenario();
    }

    @Test
    void testGetMappingKey() {
        fixture.setScenarioList(
            List.of(
                new IssueScenario(),
                new FooScenario(),
                new SubclassedFooScenario(),
                new GetFooScenario(),
                new PutFooScenario(),
                new OtherScenario()));

        assertEquals("FooScenario", mappingKeyFor(fixture, "/issues/foo"));
        assertEquals("GetFooScenario", mappingKeyFor(fixture, "/issues/foo", GET));
        assertEquals("PutFooScenario", mappingKeyFor(fixture, "/issues/foo", PUT));
        assertEquals("FooScenario", mappingKeyFor(fixture, "/issues/foo/sub", POST));
        assertEquals("OtherScenario", mappingKeyFor(fixture, "/issues/other"));
        assertEquals("IssueScenario", mappingKeyFor(fixture, "/issues/bar", GET));
        assertEquals("IssueScenario", mappingKeyFor(fixture, "/issues/bar", DELETE));
        assertEquals("default", mappingKeyFor(fixture, "/issues/bar", PUT));
        assertEquals("default", mappingKeyFor(fixture, "/issues/bar"));
        assertEquals("default", fixture.getMappingKey(null));

        fixture.setUseDefaultMapping(false);

        assertThrows(CitrusRuntimeException.class, () -> mappingKeyFor(fixture, "/issues/bar", PUT));
        assertThrows(CitrusRuntimeException.class, () -> mappingKeyFor(fixture, "/issues/bar"));
        assertThrows(CitrusRuntimeException.class, () -> fixture.getMappingKey(null));
    }


    private String mappingKeyFor(HttpRequestAnnotationScenarioMapper mapper, String path) {
        return mapper.getMappingKey(new HttpMessage().path(path));
    }

    private String mappingKeyFor(HttpRequestAnnotationScenarioMapper mapper, String path, @Nonnull HttpMethod method) {
        return mapper.getMappingKey(new HttpMessage().path(path).method(method));
    }

    @Scenario("FooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.POST)
    private static class FooScenario extends AbstractSimulatorScenario {
    }

    @RequestMapping(value = "/issues/foo/sub", method = RequestMethod.POST)
    private static class SubclassedFooScenario extends FooScenario {
    }

    @Scenario("GetFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.GET)
    private static class GetFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("PutFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.PUT)
    private static class PutFooScenario extends AbstractSimulatorScenario {
    }

    @Scenario("IssueScenario")
    @RequestMapping(value = "/issues/{name}", method = {RequestMethod.GET, RequestMethod.DELETE})
    private static class IssueScenario extends AbstractSimulatorScenario {
    }

    @Scenario("OtherScenario")
    @RequestMapping("/issues/other")
    private static class OtherScenario extends AbstractSimulatorScenario {
    }
}
