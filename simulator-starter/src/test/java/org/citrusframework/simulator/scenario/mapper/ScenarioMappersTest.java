/*
 * Copyright 2006-2019 the original author or authors.
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

package org.citrusframework.simulator.scenario.mapper;

import java.util.Arrays;
import java.util.Optional;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.http.message.HttpMessage;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.http.HttpRequestAnnotationScenarioMapper;
import org.citrusframework.simulator.http.HttpRequestPathScenarioMapper;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class ScenarioMappersTest {

    private static final String DEFAULT_SCENARIO = "default";

    @Test
    public void testMappingChain() throws Exception {
        ScenarioMappers mapperChain = ScenarioMappers.of(new HeaderMapper("foo"),
                new ContentBasedXPathScenarioMapper().addXPathExpression("/foo"),
                new ContentBasedJsonPathScenarioMapper().addJsonPathExpression("$.foo"),
                new HttpRequestPathScenarioMapper(),
                new HttpRequestAnnotationScenarioMapper(),
                new HeaderMapper("bar"));

        SimulatorConfigurationProperties configurationProperties = new SimulatorConfigurationProperties();
        configurationProperties.setDefaultScenario(DEFAULT_SCENARIO);
        mapperChain.setSimulatorConfigurationProperties(configurationProperties);

        mapperChain.setScenarioList(Arrays.asList(new FooScenario(), new BarScenario(), new OtherScenario()));
        mapperChain.afterPropertiesSet();

        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage()), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("foo").setHeader("foo", "something")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage().setHeader("foo", FooScenario.SCENARIO_NAME)), FooScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage().setHeader("foo", FooScenario.SCENARIO_NAME)
                                                                          .setHeader("bar", BarScenario.SCENARIO_NAME)), FooScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage().setHeader("foo", "something")
                                                                          .setHeader("bar", BarScenario.SCENARIO_NAME)), BarScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage().setHeader("bar", BarScenario.SCENARIO_NAME)), BarScenario.SCENARIO_NAME);

        Assert.assertEquals(mapperChain.getMappingKey(new HttpMessage().path("/other").method(HttpMethod.GET).setHeader("foo", FooScenario.SCENARIO_NAME)), FooScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new HttpMessage().path("/other").method(HttpMethod.GET).setHeader("foo", "something")), OtherScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new HttpMessage().path("/other").method(HttpMethod.GET).setHeader("bar", BarScenario.SCENARIO_NAME)), OtherScenario.SCENARIO_NAME);

        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("{ \"foo\": \"something\" }")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("{ \"foo\": \"something\" }")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("{ \"bar\": \"" + FooScenario.SCENARIO_NAME  + "\" }")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("{ \"foo\": \"" + FooScenario.SCENARIO_NAME + "\" }")), FooScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new HttpMessage("{ \"foo\": \"" + FooScenario.SCENARIO_NAME + "\" }").path("/other").method(HttpMethod.GET)), FooScenario.SCENARIO_NAME);

        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("<foo>something</foo>")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("<bar>" + FooScenario.SCENARIO_NAME  + "</bar>")), DEFAULT_SCENARIO);
        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage("<foo>" + FooScenario.SCENARIO_NAME + "</foo>")), FooScenario.SCENARIO_NAME);
        Assert.assertEquals(mapperChain.getMappingKey(new HttpMessage("<foo>" + FooScenario.SCENARIO_NAME + "</foo>").path("/other").method(HttpMethod.GET)), FooScenario.SCENARIO_NAME);

        mapperChain.setUseDefaultMapping(false);
        Assert.assertThrows(CitrusRuntimeException.class, () -> mapperChain.getMappingKey(new DefaultMessage()));
        Assert.assertThrows(CitrusRuntimeException.class, () -> mapperChain.getMappingKey(new DefaultMessage().setHeader("foo", "something")));
    }

    @Test
    public void testDefaultMapping() {
        ScenarioMappers mapperChain = ScenarioMappers.of(new HeaderMapper("foo"));
        Assert.assertThrows(CitrusRuntimeException.class, () -> mapperChain.getMappingKey(new DefaultMessage()));

        SimulatorConfigurationProperties configurationProperties = new SimulatorConfigurationProperties();
        configurationProperties.setDefaultScenario(DEFAULT_SCENARIO);
        mapperChain.setSimulatorConfigurationProperties(configurationProperties);

        Assert.assertEquals(mapperChain.getMappingKey(new DefaultMessage()), DEFAULT_SCENARIO);

        mapperChain.setUseDefaultMapping(false);
        Assert.assertThrows(CitrusRuntimeException.class, () -> mapperChain.getMappingKey(new DefaultMessage()));
    }

    private class HeaderMapper implements ScenarioMapper {

        private final String name;

        private HeaderMapper(String name) {
            this.name = name;
        }

        @Override
        public String extractMappingKey(Message request) {
            return Optional.ofNullable(request.getHeader(name))
                        .map(Object::toString)
                        .orElseThrow(CitrusRuntimeException::new);
        }
    }

    @Scenario(FooScenario.SCENARIO_NAME)
    private static class FooScenario extends AbstractSimulatorScenario {
        static final String SCENARIO_NAME = "FooScenario";
    }

    @Scenario(BarScenario.SCENARIO_NAME)
    private static class BarScenario extends AbstractSimulatorScenario {
        static final String SCENARIO_NAME = "BarScenario";
    }

    @Scenario(OtherScenario.SCENARIO_NAME)
    @RequestMapping(value = "/other", method = RequestMethod.GET)
    private static class OtherScenario extends AbstractSimulatorScenario {
        static final String SCENARIO_NAME = "OtherScenario";
    }
}
