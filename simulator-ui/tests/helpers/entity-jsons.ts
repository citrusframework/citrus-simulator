export const messageHeaderJson = [
  {
    headerId: 13,
    name: 'Content-Type',
    value: 'application/xml;charset=UTF-8',
    createdDate: '2024-08-23T08:25:31.224400Z',
    lastModifiedDate: '2024-08-23T08:25:31.224400Z',
  },
];

export const messageJson = [
  {
    messageId: 1,
    direction: 'INBOUND',
    payload: '<Default>Should trigger default scenario</Default>',
    citrusMessageId: '5605967b-bfd6-42bb-ba3b-a2404d20783a',
    headers: messageHeaderJson,
    createdDate: '2024-08-23T08:25:31.223402Z',
    lastModifiedDate: '2024-08-23T08:25:31.223402Z',
  },
];

export const scenarioExecutionJson = [
  {
    executionId: 1,
    startDate: '2024-08-23T08:25:31.950455Z',
    endDate: '2024-08-23T08:25:31.386924Z',
    scenarioName: 'Default',
    testResult: {
      id: 1,
      status: 'FAILURE',
      testName: 'Scenario(Default)',
      className: 'DefaultTestCase',
      testParameters: null,
      errorMessage: 'New Error',
      stackTrace: null,
      failureType: null,
      createdDate: '2024-08-23T08:25:31.372931Z',
      lastModifiedDate: '2024-08-23T08:25:31.373926Z',
    },
    scenarioParameters: [],
    scenarioActions: [],
    scenarioMessages: [],
  },
];

export const scenarioActionJson = [
  {
    actionId: 1,
    name: 'http:receive-request',
    startDate: '2024-08-23T08:25:31.201406Z',
    endDate: '2024-08-23T08:25:31.327926Z',
  },
];

export const testResultJson = [
  {
    id: 1,
    status: 'FAILURE',
    testName: 'Scenario(Default)',
    className: 'DefaultTestCase',
    testParameters: [],
    errorMessage: 'New Error',
    stackTrace: 'New Stacktrace',
    failureType: null,
    createdDate: '2024-08-23T08:25:31.372931Z',
    lastModifiedDate: '2024-08-23T08:25:31.373926Z',
  },
];
