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

package org.citrusframework.simulator.ws;

import org.citrusframework.spi.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.simulator.ws.WsdlScenarioGenerator.WsdlScenarioNamingStrategy.INPUT;
import static org.citrusframework.simulator.ws.WsdlScenarioGenerator.WsdlScenarioNamingStrategy.OPERATION;
import static org.citrusframework.simulator.ws.WsdlScenarioGenerator.WsdlScenarioNamingStrategy.SOAP_ACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

/**
 * @author Christoph Deppisch
 */
@ExtendWith(MockitoExtension.class)
class WsdlScenarioGeneratorTest {

    private static final String SCENARIO_INPUT = format("<v1:TestRequest name=\"string\" id=\"100\" flag=\"(true|false)\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">%n" +
        "  <v1:name>string</v1:name>%n" +
        "  <v1:id>100</v1:id>%n" +
        "  <v1:flag>(true|false)</v1:flag>%n" +
        "  <v1:restricted>stringstri</v1:restricted>%n" +
        "</v1:TestRequest>");
    public static final Pattern SCENARIO_INPUT_PATTERN = Pattern.compile(SCENARIO_INPUT);

    private static final String SCENARIO_OUTPUT = format("<v1:TestResponse name=\"string\" id=\"100\" flag=\"(true|false)\" xmlns:v1=\"http://www.citrusframework.org/schema/samples/TestService/v1\">%n" +
        "  <v1:name>string</v1:name>%n" +
        "  <v1:id>100</v1:id>%n" +
        "  <v1:flag>(true|false)</v1:flag>%n" +
        "  <v1:restricted>stringstri</v1:restricted>%n" +
        "</v1:TestResponse>");
    public static final Pattern SCENARIO_OUTPUT_PATTERN = Pattern.compile(SCENARIO_OUTPUT);

    @Mock
    private ConfigurableListableBeanFactory beanFactoryMock;

    @Mock
    private DefaultListableBeanFactory beanRegistryMock;

    private WsdlScenarioGenerator fixture;

    static Stream<Arguments> data() {
        return Stream.of(
            arguments("TestRequest", INPUT, "/TestService/test", SCENARIO_INPUT, SCENARIO_OUTPUT),
            arguments("test", OPERATION, "/TestService/test", SCENARIO_INPUT, SCENARIO_OUTPUT),
            arguments("/TestService/test", SOAP_ACTION, "/TestService/test", SCENARIO_INPUT, SCENARIO_OUTPUT)
        );
    }

    @BeforeEach
    void beforeEachSetup() {
        fixture = new WsdlScenarioGenerator(new Resources.ClasspathResource("schema/TestService.wsdl"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void testGenerateScenarios(String scenarioName, WsdlScenarioGenerator.WsdlScenarioNamingStrategy namingStrategy, String soapAction, String input, String output) {
        fixture.setNamingStrategy(namingStrategy);

        doAnswer(invocation -> {
            WsdlOperationScenario scenario = (WsdlOperationScenario) invocation.getArguments()[1];

            assertEquals(soapAction, scenario.getSoapAction());
            assertThat(scenario.getInput()).matches(SCENARIO_INPUT_PATTERN);
            assertThat(scenario.getOutput()).matches(SCENARIO_OUTPUT_PATTERN);

            return null;
        }).when(beanFactoryMock).registerSingleton(eq(scenarioName), any(WsdlOperationScenario.class));

        fixture.postProcessBeanFactory(beanFactoryMock);

        verify(beanFactoryMock).registerSingleton(eq(scenarioName), any(WsdlOperationScenario.class));
    }

    @ParameterizedTest
    @MethodSource("data")
    void testGenerateScenariosWithRegistry(String scenarioName, WsdlScenarioGenerator.WsdlScenarioNamingStrategy namingStrategy, String soapAction, String input, String output) {
        fixture.setNamingStrategy(namingStrategy);

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            assertEquals(soapAction, scenario.getPropertyValues().get("soapAction"));
            assertThat(scenario.getPropertyValues().get("input")).asString().matches(SCENARIO_INPUT_PATTERN);
            assertThat(scenario.getPropertyValues().get("output")).asString().matches(SCENARIO_OUTPUT_PATTERN);
            assertNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            assertNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));

        fixture.postProcessBeanFactory(beanRegistryMock);

        verify(beanRegistryMock).registerBeanDefinition(eq(scenarioName), any(BeanDefinition.class));
    }

    @Test
    void generateScenariosWithDataDictionaries() {
        doReturn(true).when(beanRegistryMock).containsBeanDefinition("inboundXmlDataDictionary");
        doReturn(true).when(beanRegistryMock).containsBeanDefinition("outboundXmlDataDictionary");

        doAnswer(invocation -> {
            BeanDefinition scenario = (BeanDefinition) invocation.getArguments()[1];

            assertEquals("/TestService/test", scenario.getPropertyValues().get("soapAction"));
            assertThat(scenario.getPropertyValues().get("input")).asString().matches(SCENARIO_INPUT_PATTERN);
            assertThat(scenario.getPropertyValues().get("output")).asString().matches(SCENARIO_OUTPUT_PATTERN);
            assertNotNull(scenario.getPropertyValues().get("inboundDataDictionary"));
            assertNotNull(scenario.getPropertyValues().get("outboundDataDictionary"));

            return null;
        }).when(beanRegistryMock).registerBeanDefinition(eq("TestRequest"), any(BeanDefinition.class));

        fixture.postProcessBeanFactory(beanRegistryMock);

        verify(beanRegistryMock).registerBeanDefinition(eq("TestRequest"), any(BeanDefinition.class));
    }
}
