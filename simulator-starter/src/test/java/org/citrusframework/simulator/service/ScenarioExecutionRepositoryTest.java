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
package org.citrusframework.simulator.service;

import jakarta.persistence.EntityManager;
import org.citrusframework.DefaultTestCase;
import org.citrusframework.simulator.SimulatorAutoConfiguration;
import org.citrusframework.simulator.config.SimulatorConfigurationProperties;
import org.citrusframework.simulator.model.Message.Direction;
import org.citrusframework.simulator.model.ScenarioExecution;
import org.citrusframework.simulator.model.ScenarioExecution.Status;
import org.citrusframework.simulator.model.ScenarioExecutionFilter;
import org.citrusframework.simulator.repository.ScenarioExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;

@DataJpaTest
@ContextConfiguration(classes = {SimulatorAutoConfiguration.class})
public class ScenarioExecutionRepositoryTest extends AbstractTestNGSpringContextTests {

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
    private EntityManager entityManager;

    @Autowired
    private ScenarioExecutionRepository scenarioExecutionRepository;

    @Test
    void testFindByScenarioName() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();

        String uniqueScenarioName = TEST_SCENARIO + UUID.randomUUID().toString();
        createTestScenarioExecution(uniqueScenarioName, inUid, PAYLOAD, outUid, PAYLOAD, Status.SUCCESS);

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setScenarioName(uniqueScenarioName);

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());
    }

    @Test
    void testFindByScenarioStatus() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();

        String uniqueScenarioName = TEST_SCENARIO + UUID.randomUUID();
        createTestScenarioExecution(uniqueScenarioName, inUid + 1, PAYLOAD, outUid + 1, PAYLOAD, Status.SUCCESS);
        Long failedScenarioExecutionId = createTestScenarioExecution(uniqueScenarioName, inUid + 2, PAYLOAD, outUid + 2, PAYLOAD, Status.FAILED);

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setExecutionStatus(new Status[] {Status.FAILED});

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        List<ScenarioExecution> result = scenarioExecutionRepository.find(queryFilter);
        assertEquals(1, result.size());
        assertEquals(failedScenarioExecutionId, result.get(0).getExecutionId());
    }

    @Test
    void testFindByHeader() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();
        createTestScenarioExecution(inUid, PAYLOAD, outUid, PAYLOAD);

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1 + ":" + inUid);


        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1 + ":" + inUid + "mod");

        assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
    }

    @Test
    void testFindByHeaderMulti() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();
        createTestScenarioExecution(inUid, PAYLOAD, outUid, PAYLOAD);

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1 + ":" + inUid + ";" + IN_HEADER_NAME2 + ":" + inUid + "_2");

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setHeaderFilter(IN_HEADER_NAME1 + ":" + inUid + ";" + IN_HEADER_NAME2 + ":" + inUid + "_3");

        assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
    }

    @Test
    void testFindByPayload() {
        String inUid = UUID.randomUUID().toString();
        String outUid = UUID.randomUUID().toString();
        String inPayload = PAYLOAD + inUid + "-in";
        String outPayload = PAYLOAD + outUid + "-out";

        createTestScenarioExecution(inUid, inPayload, outUid, outPayload);

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        scenarioExecutionFilter.setContainingText(inPayload);
        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setContainingText(inPayload + "mod");
        assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionInbound(false);
        scenarioExecutionFilter.setContainingText(inPayload);
        assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionInbound(true);
        scenarioExecutionFilter.setContainingText(inPayload);
        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionOutbound(true);
        scenarioExecutionFilter.setContainingText(outPayload);
        assertEquals(1, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setDirectionOutbound(false);
        scenarioExecutionFilter.setContainingText(outPayload);
        assertEquals(0, scenarioExecutionRepository.find(queryFilter).size());
    }

    @Test
    void testPaging() {
        String uniquePayload = "PagingPayload" + UUID.randomUUID().toString();
        for (int i = 0; i < 100; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload + "-in", UUID.randomUUID().toString(), uniquePayload + "-out");
        }

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setPageNumber(0);
        scenarioExecutionFilter.setPageSize(33);

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);
        List<ScenarioExecution> result = scenarioExecutionRepository.find(queryFilter);

        assertEquals(result.size(), 33);
    }

    @Test
    void testFindByDate() {
        String uniquePayload = "FindByDatePayload" + UUID.randomUUID();

        Instant t1 = Instant.now();

        int batch1Size = 100;
        for (int i = 0; i < batch1Size; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload + "-in", UUID.randomUUID().toString(), uniquePayload + "-out");
        }

        entityManager.flush();

        Instant t2 = Instant.now();

        int batch2Size = 50;
        for (int i = 0; i < batch2Size; i++) {
            createTestScenarioExecution(UUID.randomUUID().toString(), uniquePayload + "-in", UUID.randomUUID().toString(), uniquePayload + "-out");
        }

        entityManager.flush();

        Instant t3 = Instant.now();

        ScenarioExecutionFilter scenarioExecutionFilter = new ScenarioExecutionFilter();
        scenarioExecutionFilter.setPageNumber(0);
        scenarioExecutionFilter.setPageSize(1000);

        ScenarioExecutionFilter queryFilter = queryFilterAdapterFactory.getQueryAdapter(scenarioExecutionFilter);

        scenarioExecutionFilter.setFromDate(t1);
        scenarioExecutionFilter.setToDate(t2);
        assertEquals(batch1Size, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setFromDate(t1);
        scenarioExecutionFilter.setToDate(t3);
        assertEquals(batch1Size + batch2Size, scenarioExecutionRepository.find(queryFilter).size());

        scenarioExecutionFilter.setFromDate(t2);
        scenarioExecutionFilter.setToDate(t3);
        assertEquals(batch2Size, scenarioExecutionRepository.find(queryFilter).size());
    }

    private Long createTestScenarioExecution(String inUid, String inPayload, String outUid, String outPayload) {
        return createTestScenarioExecution(TEST_SCENARIO, inUid, inPayload, outUid, outPayload, Status.SUCCESS);
    }

    private Long createTestScenarioExecution(String scenarioName, String inUid, String inPayload, String outUid, String outPayload, Status status) {
        Map<String, Object> inHeaders = new HashMap<>();
        inHeaders.put(IN_HEADER_NAME1, inUid);
        inHeaders.put(IN_HEADER_NAME2, inUid + "_2");

        ScenarioExecution scenarioExecution = activityService.createExecutionScenario(scenarioName, Collections.emptySet());
        activityService.saveScenarioMessage(scenarioExecution.getExecutionId(), Direction.INBOUND, inPayload, inUid, inHeaders);

        Map<String, Object> outHeaders = new HashMap<>();
        outHeaders.put(OUT_HEADER_NAME1, outUid);
        outHeaders.put(OUT_HEADER_NAME2, outUid + "_2");
        activityService.saveScenarioMessage(scenarioExecution.getExecutionId(), Direction.OUTBOUND, outPayload, outUid, outHeaders);

        DefaultTestCase testCase = new DefaultTestCase();
        testCase.getVariableDefinitions().put(ScenarioExecution.EXECUTION_ID, scenarioExecution.getExecutionId());

        if (Status.SUCCESS == status) {
            activityService.completeScenarioExecutionSuccess(testCase);
        } else if (Status.FAILED == status) {
            activityService.completeScenarioExecutionFailure(testCase, null);
        }

        return scenarioExecution.getExecutionId();
    }
}
