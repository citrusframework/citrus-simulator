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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.citrusframework.http.message.HttpMessage;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.http.HttpRequestAnnotationScenarioMapperIT.AspectTestConfiguration;
import org.citrusframework.simulator.scenario.AbstractSimulatorScenario;
import org.citrusframework.simulator.scenario.Scenario;
import org.citrusframework.simulator.scenario.SimulatorScenario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.PUT;

@ExtendWith(SpringExtension.class)
@Import(AspectTestConfiguration.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class HttpRequestAnnotationScenarioMapperIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HttpRequestAnnotationScenarioMapper fixture;

    @Test
    void testGetMappingKeyFromProxiedScenario() {
        Collection<SimulatorScenario> scenarios = applicationContext.getBeansOfType(SimulatorScenario.class).values();

        assertThat(scenarios).hasSize(1);

        assertThat(AopUtils.isAopProxy(scenarios.iterator().next())).isTrue();
        fixture.setScenarioList(new ArrayList<>(scenarios));
        assertEquals("PutFooScenario", fixture.getMappingKey(new HttpMessage().path("/issues/foo").method(PUT)));
    }

    @Scenario("PutFooScenario")
    @RequestMapping(value = "/issues/foo", method = RequestMethod.PUT)
    public static class PutFooScenario extends AbstractSimulatorScenario {
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    public static class AspectTestConfiguration {

        @Bean
        public SimulatorScenario putFooScenario() {
            return new PutFooScenario();
        }

        @Bean
        public ScenarioWrappingAspect myAspect() {
            return new ScenarioWrappingAspect();
        }

        @Bean
        public HttpRequestAnnotationScenarioMapper httpRequestAnnotationScenarioMapper() {
            return new HttpRequestAnnotationScenarioMapper();
        }

        @Bean
        public SimulatorConfigurationProperties simulatorConfigurationProperties() {
            return new SimulatorConfigurationProperties();
        }
    }

    @Aspect
    public static class ScenarioWrappingAspect {

        private static final String RUN_SCENARIO_POINTCUT =
            "within(org.citrusframework.simulator.scenario.SimulatorScenario+) && execution(* run(org.citrusframework.simulator.scenario.ScenarioRunner))";

        @Around(RUN_SCENARIO_POINTCUT)
        public Object interceptScenarioExecution(ProceedingJoinPoint joinPoint) throws Throwable {
            return joinPoint.proceed();
        }
    }
}
