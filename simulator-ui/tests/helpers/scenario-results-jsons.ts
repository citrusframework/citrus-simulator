export const twoScenarioExecutions = [
  {
    executionId: 630650,
    startDate: '2024-06-20T10:05:42.239174Z',
    endDate: '2024-06-20T10:05:42.608135Z',
    scenarioName: 'PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}',
    testResult: {
      id: 630101,
      status: 'SUCCESS',
      testName: 'PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}',
      className: 'DefaultTestCase',
      testParameters: null,
      errorMessage: null,
      stackTrace: null,
      failureType: null,
      createdDate: '2024-06-20T08:05:42.603448Z',
      lastModifiedDate: '2024-06-20T08:05:42.603448Z',
    },
    scenarioParameters: [],
    scenarioActions: [
      {
        actionId: 3150466,
        name: 'AbstractOperationSimulatorScenario$$Lambda/0x00007feea178ae98',
        startDate: '2024-06-20T10:05:42.253636Z',
        endDate: '2024-06-20T10:05:42.270870Z',
      },
      {
        actionId: 3150467,
        name: 'http:receive-request',
        startDate: '2024-06-20T10:05:42.283446Z',
        endDate: '2024-06-20T10:05:42.415375Z',
      },
      {
        actionId: 3150468,
        name: 'MatchRequestAction',
        startDate: '2024-06-20T10:05:42.427437Z',
        endDate: '2024-06-20T10:05:42.482621Z',
      },
      {
        actionId: 3150469,
        name: 'GetTestContextAction',
        startDate: '2024-06-20T10:05:42.495890Z',
        endDate: '2024-06-20T10:05:42.513154Z',
      },
      {
        actionId: 3150470,
        name: 'http:send-response',
        startDate: '2024-06-20T10:05:42.532637Z',
        endDate: '2024-06-20T10:05:42.592635Z',
      },
    ],
    scenarioMessages: [
      {
        messageId: 1260915,
        direction: 'INBOUND',
        payload: '{\n                            "ids": [1,2,3]\n                            }',
        citrusMessageId: '23be1a00-7ce4-4387-8d1a-46e4b36d440d',
        headers: null,
        createdDate: '2024-06-20T08:05:42.299317Z',
        lastModifiedDate: '2024-06-20T08:05:42.299317Z',
      },
      {
        messageId: 1260916,
        direction: 'OUTBOUND',
        payload:
          '{\n            "id1": "1",\n            "id2": "2",\n            "id3": "3",\n            "q1": "q1Value",\n            "q2": "q2Value",\n            "h1": "h1Value",\n            "h2": "h2Value",\n            "body": {"ids":[1,2,3]}\n}',
        citrusMessageId: '864e1880-9d16-4907-b7f0-48c52cec780f',
        headers: null,
        createdDate: '2024-06-20T08:05:42.549112Z',
        lastModifiedDate: '2024-06-20T08:05:42.549112Z',
      },
    ],
  },
  {
    executionId: 635675,
    startDate: '2024-06-20T10:43:15.172667Z',
    endDate: '2024-06-20T10:43:15.493745Z',
    scenarioName: 'PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}',
    testResult: {
      id: 635128,
      status: 'SUCCESS',
      testName: 'PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}',
      className: 'DefaultTestCase',
      testParameters: null,
      errorMessage: null,
      stackTrace: null,
      failureType: null,
      createdDate: '2024-06-20T08:43:15.489557Z',
      lastModifiedDate: '2024-06-20T08:43:15.489557Z',
    },
    scenarioParameters: [],
    scenarioActions: [
      {
        actionId: 3175577,
        name: 'AbstractOperationSimulatorScenario$$Lambda/0x00007f8a4178c748',
        startDate: '2024-06-20T10:43:15.187104Z',
        endDate: '2024-06-20T10:43:15.204815Z',
      },
      {
        actionId: 3175579,
        name: 'http:receive-request',
        startDate: '2024-06-20T10:43:15.217532Z',
        endDate: '2024-06-20T10:43:15.300350Z',
      },
      {
        actionId: 3175593,
        name: 'MatchRequestAction',
        startDate: '2024-06-20T10:43:15.313720Z',
        endDate: '2024-06-20T10:43:15.376332Z',
      },
      {
        actionId: 3175607,
        name: 'GetTestContextAction',
        startDate: '2024-06-20T10:43:15.389336Z',
        endDate: '2024-06-20T10:43:15.404897Z',
      },
      {
        actionId: 3175615,
        name: 'http:send-response',
        startDate: '2024-06-20T10:43:15.423314Z',
        endDate: '2024-06-20T10:43:15.473550Z',
      },
    ],
    scenarioMessages: [
      {
        messageId: 1270958,
        direction: 'INBOUND',
        payload: '{\n                            "ids": [1,2,3]\n                            }',
        citrusMessageId: 'dfb36c8f-ee48-4283-ae51-efc302014004',
        headers: null,
        createdDate: '2024-06-20T08:43:15.231392Z',
        lastModifiedDate: '2024-06-20T08:43:15.231392Z',
      },
      {
        messageId: 1270971,
        direction: 'OUTBOUND',
        payload:
          '{\n            "id1": "1",\n            "id2": "2",\n            "id3": "3",\n            "q1": "q1Value",\n            "q2": "q2Value",\n            "h1": "h1Value",\n            "h2": "h2Value",\n            "body": {"ids":[1,2,3]}\n}',
        citrusMessageId: '0eedfdec-82eb-446c-b38c-2cfe533e372e',
        headers: null,
        createdDate: '2024-06-20T08:43:15.437484Z',
        lastModifiedDate: '2024-06-20T08:43:15.437484Z',
      },
    ],
  },
];

export const scenarioExecutionJsonWithoutDetails = [
  {
    executionId: 752603,
    startDate: '2024-06-27T10:59:03.872021Z',
    endDate: '2024-06-27T11:05:21.168103Z',
    scenarioName: 'Default',
    testResult: {
      id: 752026,
      status: 'FAILURE',
      errorMessage:
        "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
      createdDate: '2024-06-27T08:59:04.159168Z',
    },
  },
];

export const scenarioExecutionJsonWithDetails = {
  executionId: 752603,
  startDate: '2024-06-27T10:59:03.872021Z',
  endDate: '2024-06-27T11:05:21.168103Z',
  scenarioName: 'Default',
  testResult: {
    id: 752026,
    status: 'FAILURE',
    errorMessage:
      "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
    stackTrace:
      'org.citrusframework.exceptions.CitrusRuntimeException: It is the courage to continue that counts. at org.citrusframework.actions.FailAction.doExecute(FailAction.java:43) at org.citrusframework.actions.AbstractTestAction.execute(AbstractTestAction.java:59) at org.citrusframework.DefaultTestCase.executeAction(DefaultTestCase.java:190) at org.citrusframework.DefaultTestCaseRunner.run(DefaultTestCaseRunner.java:145) at org.citrusframework.simulator.scenario.ScenarioRunner.run(ScenarioRunner.java:79) at org.citrusframework.TestActionRunner.$(TestActionRunner.java:51) at org.citrusframework.simulator.sample.scenario.FailScenario.run(FailScenario.java:49) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.createAndRunScenarioRunner(DefaultScenarioExecutorService.java:147) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.startScenario(DefaultScenarioExecutorService.java:116) at org.citrusframework.simulator.service.runner.AsyncScenarioExecutorService.lambda$startScenarioAsync$0(AsyncScenarioExecutorService.java:126) at java.base',
    createdDate: '2024-06-27T08:59:04.159168Z',
  },
  scenarioParameters: [
    {
      parameterId: 0,
      name: 'scenario parameter name',
      controlType: 'Control Type of parameter',
      value: 'scenario parameter value',
      options: [
        {
          key: 'random key',
          value: 'random value',
        },
      ],
      createdDate: '2024-06-27T09:27:02.286Z',
      lastModifiedDate: '2024-06-27T09:27:02.286Z',
    },
  ],
  scenarioActions: [
    {
      actionId: 29,
      name: 'http:receive-request',
      startDate: '2024-06-27T15:10:03.616968Z',
      endDate: '2024-08-15T15:10:03.632568Z',
    },
    {
      actionId: 30,
      name: 'echo',
      startDate: '2024-06-27T15:10:03.632568Z',
      endDate: '2024-06-27T15:10:03.648188Z',
    },
  ],
  scenarioMessages: [
    {
      messageId: 20,
      direction: 'INBOUND',
      payload: '',
      citrusMessageId: '0fa15e84-422f-4a61-a587-ea33d12e4b38',
      headers: null,
      createdDate: '2024-06-27T13:10:03.632568Z',
      lastModifiedDate: '2024-06-27T13:10:03.632568Z',
    },
  ],
};

export const messageJson = {
  messageId: 20,
  direction: 'INBOUND',
  payload: '',
  citrusMessageId: 'c65cdf92-7075-44d6-b0f2-42a556d12f80',
  headers: [
    {
      headerId: 214,
      name: 'accept',
      value: 'application/json',
      createdDate: '2024-08-22T08:38:56.708514Z',
      lastModifiedDate: '2024-08-22T08:38:56.708514Z',
    },
    {
      headerId: 207,
      name: 'deflect-encoding',
      value: 'gzip, x-gzip, deflate',
      createdDate: '2024-08-22T08:38:56.708514Z',
      lastModifiedDate: '2024-08-22T08:38:56.708514Z',
    },
  ],
  createdDate: '2024-08-22T08:38:56.632568Z',
  lastModifiedDate: '2024-08-22T08:38:56.632568Z',
};

export const messageHeaderJson = {
  headerId: 214,
  name: 'accept',
  value: 'application/json',
  createdDate: '2024-08-22T08:38:56.708514Z',
  lastModifiedDate: '2024-08-22T08:38:56.708514Z',
};

export const scenarioActionJson = {
  actionId: 29,
  name: 'http:receive-request',
  startDate: '2024-06-27T15:10:03.616968Z',
  endDate: '2024-08-15T15:10:03.632568Z',
};
