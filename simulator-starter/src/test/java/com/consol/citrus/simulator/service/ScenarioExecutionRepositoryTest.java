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
package com.consol.citrus.simulator.service;

import com.consol.citrus.DefaultTestCase;
import com.consol.citrus.simulator.SimulatorAutoConfiguration;
import com.consol.citrus.simulator.config.SimulatorConfigurationProperties;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.model.ScenarioExecution;
import com.consol.citrus.simulator.model.ScenarioExecutionFilter;
import com.consol.citrus.simulator.repository.ScenarioExecutionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes= {SimulatorAutoConfiguration.class})
public class ScenarioExecutionRepositoryTest {
    
    private static final String TEST_SCENARIO= "test-scenario";

    private static final SimulatorConfigurationProperties PROPERTIES = new SimulatorConfigurationProperties();
    
    private static final QueryFilterAdapterFactory queryFilterAdapterFactory = new QueryFilterAdapterFactory(PROPERTIES);
    
    private static final String PAYLOAD = "test-payload";
    
    private static final String IN_HEADER_NAME1 = "IH1";
    private static final String IN_HEADER_NAME2 = "IH2";
    
    private static final String OUT_HEADER_NAME1 = "OH1";
    private static final String OUT_HEADER_NAME2 = "OH2";
    
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    
    @Test
    void testFindByHeader() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();
        createTestScenarioExecution(inUid, PAYLOAD, outUid, PAYLOAD);
        
        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1+":"+inUid);

        
        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);
        
        Assertions.assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1+":"+inUid+"mod");
        
        Assertions.assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
    }
    
    @Test
    void testFindByPayload() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();
        String inPayload = PAYLOAD+inUid+"-in";
        String outPayload = PAYLOAD+outUid+"-out";
        
        createTestScenarioExecution(inUid, inPayload, outUid, outPayload);
        
        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        
        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);
        
        scenarioExecutionFilter.setContainingText(inPayload);
        Assertions.assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());
        
        scenarioExecutionFilter.setContainingText(inPayload+"mod");
        Assertions.assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
        
        scenarioExecutionFilter.setDirectionInbound(false);
        scenarioExecutionFilter.setContainingText(inPayload);
        Assertions.assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
        
        scenarioExecutionFilter.setDirectionInbound(true);
        scenarioExecutionFilter.setContainingText(inPayload);
        Assertions.assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionOutbound(true);
        scenarioExecutionFilter.setContainingText(outPayload);
        Assertions.assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionOutbound(false);
        scenarioExecutionFilter.setContainingText(outPayload);
        Assertions.assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
        
    }
    
    @Test
    void testPaging() {
        String uniquePayload = "PagingPayload" + UUID.randomUUID().toString();
        for (int i = 0; i < 100; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload+"-in", UUID.randomUUID().toString(), uniquePayload+"-out");
        }

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setPageNumber(0);
        scenarioExecutionFilter.setPageSize(33);
        
        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);
        List<ScenarioExecution> result = scenarioExecutionRepository.find(queryFilter);

        Assertions.assertEquals(33, result.size());

    }
    
    @Test
    void testFindByDate() {

        Date t1 = new Date();
        String uniquePayload = "FindByDatePayload" + UUID.randomUUID().toString();
        for (int i = 0; i < 100; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload+"-in", UUID.randomUUID().toString(), uniquePayload+"-out");
        }

        Date t2 = new Date();

        for (int i = 0; i < 50; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload+"-in", UUID.randomUUID().toString(), uniquePayload+"-out");
        }

        Date t3 = new Date();

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setPageNumber(0);
        scenarioExecutionFilter.setPageSize(1000);

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        scenarioExecutionFilter.setFromDate(t1);
        scenarioExecutionFilter.setToDate(t2);
        Assertions.assertEquals(100, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setFromDate(t1);
        scenarioExecutionFilter.setToDate(t3);
        Assertions.assertEquals(150, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setFromDate(t2);
        scenarioExecutionFilter.setToDate(t3);
        Assertions.assertEquals(50, scenarioExecutionRepository.find(queryFilter).size());

    }
    
    private void createTestScenarioExecution(String inUid, String inPayload, String outUid, String outPayload) {
     
        Map<String, Object> inHeaders = new HashMap<String, Object>();
        inHeaders.put(IN_HEADER_NAME1, inUid);
        inHeaders.put(IN_HEADER_NAME2, inUid+"_2");

        ScenarioExecution scenarioExecution = activityService.createExecutionScenario(TEST_SCENARIO, Collections.emptySet());
        activityService.saveScenarioMessage(scenarioExecution.getExecutionId(), Direction.INBOUND, inPayload, inUid, inHeaders);

        Map<String, Object> outHeaders = new HashMap<String, Object>();
        outHeaders.put(OUT_HEADER_NAME1, outUid);
        outHeaders.put(OUT_HEADER_NAME2, outUid+"_2");
        activityService.saveScenarioMessage(scenarioExecution.getExecutionId(), Direction.OUTBOUND, outPayload, outUid, outHeaders);
        
        DefaultTestCase testCase = new DefaultTestCase();
        testCase.getVariableDefinitions().put(ScenarioExecution.EXECUTION_ID, scenarioExecution.getExecutionId());
        activityService.completeScenarioExecutionSuccess(testCase);
    }

}
