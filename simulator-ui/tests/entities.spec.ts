import {expect, Page, test} from "@playwright/test";
import {EntityPageContentObject} from "./helpers/helper-interfaces";
import {
  messageHeaderJson,
  messageJson,
  scenarioActionJson,
  scenarioExecutionJson, testParameterJson,
  testResultJson
} from "./helpers/entity-jsons";
import {mockBackendResponse} from "./helpers/helper-functions";


const exampleDate = '23 Aug 2024 08:25:31';
const entityPageContentMap: EntityPageContentObject[] = [
  {
    apiUrl: '**/api/messages*',
    entityUrl: 'http://localhost:9000/message',
    contentJson: messageJson,
    locators: [
      'th :text("Direction")',
      'th :text("Payload")',
      'th :text("Citrus Message Id")',
      'th :text("Scenario Execution")',
      'th :text("Created Date")',
      'th :text("Last Modified Date")',
    ],
    testIdsAndExpectedValues: [
      {id: 'messageEntityMessageId', value: '1'},
      {id: 'messageEntityMessageDirection', value: 'INBOUND'},
      {id: 'messageEntityMessagePayload', value: '<Default>Should trigger default scenario</Default>'},
      {id: 'messageEntityMessageCitrusMessage', value: '5605967b-bfd6-42bb-ba3b-a2404d20783a'},
      {id: 'messageEntityMessageCreatedDate', value: exampleDate},
      {id: 'messageEntityMessageLastModified', value: exampleDate}],
    testIdToBeVisible: [
      'filterOtherEntityButton',
    ]
  },
  {
    apiUrl: '**/api/message-headers*',
    entityUrl: 'http://localhost:9000/message-header',
    contentJson: messageHeaderJson,
    locators: ['th :text("Name")', 'th :text("Value")'],
    testIdsAndExpectedValues: [
      {id: 'messageHeaderEntityId', value: '13'},
      {id: 'messageHeaderEntityName', value: 'Content-Type'},
      {id: 'messageHeaderEntityValue', value: 'application/xml;charset=UTF-8'}],
    testIdToBeVisible: []
  },
  {
    apiUrl: '**/api/scenario-executions*',
    entityUrl: 'http://localhost:9000/scenario-execution',
    contentJson: scenarioExecutionJson,
    locators: [
      'th :text("Name")',
      'th :text("Start Date")',
      'th :text("End Date")',
      'th :text("Status")',
      'th :text("Error Message")',
    ],
    testIdsAndExpectedValues: [
      {id: 'scenarioExecutionEntityScenarioExecutionLink', value: '1'},
      {id: 'scenarioExecutionEntityScenarioName', value: 'Default'},
      {id: 'scenarioExecutionEntityStartDate', value: exampleDate},
      {id: 'scenarioExecutionEntityEndDate', value: exampleDate},
      {id: 'scenarioExecutionEntityStatus', value: 'FAILURE'},
      {id: 'scenarioExecutionEntityTestResult', value: 'New Error'},
    ],
    testIdToBeVisible: []
  },
  {
    apiUrl: '**/api/scenario-actions*',
    entityUrl: 'http://localhost:9000/scenario-action',
    contentJson: scenarioActionJson,
    locators: ['th :text("Name")', 'th :text("Start Date")', 'th :text("End Date")', 'th :text("Scenario Execution")'],
    testIdsAndExpectedValues: [
      {id: 'scenarioActionEntitiesId', value: '1'},
      {id: 'scenarioActionEntitiesName', value: 'http:receive-request'},
      {id: 'scenarioActionEntitiesStartDate', value: exampleDate},
      {id: 'scenarioActionEntitiesEndDate', value: exampleDate},
    ],
    testIdToBeVisible: []
  },
  {
    apiUrl: '**/api/test-results*',
    entityUrl: 'http://localhost:9000/test-result',
    contentJson: testResultJson,
    locators: [
      'th :text("Status")',
      'th :text("Test Name")',
      'th :text("Class Name")',
      'th :text("Error Message")',
      'th :text("Stack Trace")'],
    testIdsAndExpectedValues: [
      {id: 'testResultEntitiesId', value: '1'},
      {id: 'testResultEntitiesStatus', value: 'FAILURE'},
      {id: 'testResultEntitiesTestName', value: 'Scenario(Default)'},
      {id: 'testResultEntitiesClassName', value: 'DefaultTestCase'},
      {id: 'testResultEntitiesErrorMessage', value: 'New Error'},
      {id: 'testResultEntitiesStackTrace', value: 'New Stacktrace'},
      {id: 'testResultEntitiesFailureType', value: ''},
      {id: 'testResultEntitiesCreatedDate', value: exampleDate},
      {id: 'testResultEntitiesLastModifiedDate', value: exampleDate},
    ],
    testIdToBeVisible: []
  },
  {
    apiUrl: '**/api/test-parameters*',
    entityUrl: 'http://localhost:9000/test-parameter',
    contentJson: testParameterJson,
    locators: [
      'th :text("Key")',
      'th :text("Value")',
      'th :text("Test Result")',
      'th :text("Created Date")',
      'th :text("Last Modified Date")'
    ],
    testIdsAndExpectedValues: [
      {id: 'testParameterEntityKey', value: 'test key'},
      {id: 'testParameterEntityValue', value: 'test value'},
      {id: 'testParameterEntityTestResultLink', value: '0'},
      {id: 'testParameterEntityCreatedDate', value: exampleDate},
      {id: 'testParameterEntityLastModifiedDate', value: exampleDate},
    ],
    testIdToBeVisible: ['', '']
  }
]

//the test steps are very repetitive and could be done in one loop... would this work with playwright?
//or should there be a separate test file for every entity page?

//should these be two separate tests?
test('should display table of messages and refresh button should work', async ({page}) => {
  //'first test'
  const contentObject = entityPageContentMap[0];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await checkEntityPageContentValueAndVisibility(page, contentObject);
  //'second test'
  await checkIfRefreshButtonWorks(page, contentObject);
})

test('should show message headers when clicking button on message row', async ({page}) => {
  const contentObject = entityPageContentMap[0];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await page.getByTestId('filterOtherEntityButton').nth(0).click();
  await expect(page.getByTestId('filterValue')).toHaveText('messageId.in: 1');
  await expect(page).toHaveURL('http://localhost:9000/message-header?filter%5BmessageId.in%5D=1');
  //eventually test if gui-element is visible too?
})

test('should display table of message headers and refresh button should work', async ({page}) => {
  const contentObject = entityPageContentMap[1];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);
  await page.goto(contentObject.entityUrl);

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);

  await checkEntityPageContentValueAndVisibility(page, contentObject);
  await checkIfRefreshButtonWorks(page, contentObject);
})

test('should display table of scenario executions', async ({page}) => {
  const contentObject = entityPageContentMap[2];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson)

  await page.goto(contentObject.entityUrl);

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await checkEntityPageContentValueAndVisibility(page, contentObject);
  await checkIfRefreshButtonWorks(page, contentObject);
})

test('should display table of scenario actions', async ({page}) => {
  const contentObject = entityPageContentMap[3];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await checkEntityPageContentValueAndVisibility(page, contentObject);
  await checkIfRefreshButtonWorks(page, contentObject);
})

test('should display table of test results', async ({page}) => {
  const contentObject = entityPageContentMap[4];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await checkEntityPageContentValueAndVisibility(page, contentObject);
  await checkIfRefreshButtonWorks(page, contentObject);
})

test('should show test parameters when clicking on button in test results row', async ({page}) => {
  const contentObject = entityPageContentMap[4];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await page.getByTestId('testParametersButton').click();
  await expect(page.getByTestId('filterValue')).toHaveText('testResultId.in: 1');
  await expect(page).toHaveURL('http://localhost:9000/test-parameter?filter%5BtestResultId.in%5D=1');
})

test('should display table of test parameters', async ({page}) => {
  const contentObject = entityPageContentMap[5];
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.goto(contentObject.entityUrl);

  await checkEntityPageContentValueAndVisibility(page, contentObject);
  await checkIfRefreshButtonWorks(page, contentObject);
})


const checkEntityPageContentValueAndVisibility = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  for (const locator of contentObject.locators) {
    await expect(page.locator(locator)).toHaveCount(1);
  }
  for (const testId of contentObject.testIdsAndExpectedValues) {
    await expect(page.getByTestId(testId.id)).toHaveText(testId.value);
  }
  for (const testId of contentObject.testIdToBeVisible) {
    await expect(page.getByTestId(testId)).toBeVisible();
  }
}

const checkIfRefreshButtonWorks = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  contentObject.contentJson.pop();
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.getByTestId('refreshListButton').click();

  await checkContentAfterRefresh(page, contentObject);
}


const checkContentAfterRefresh = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  for (const locator of contentObject.locators) {
    await expect(page.locator(locator)).toHaveCount(0);
  }
  for (const testId of contentObject.testIdsAndExpectedValues) {
    await expect(page.getByTestId(testId.id)).toBeHidden();
  }
  for (const testId of contentObject.testIdToBeVisible) {
    await expect(page.getByTestId(testId)).toBeHidden();
  }
}
