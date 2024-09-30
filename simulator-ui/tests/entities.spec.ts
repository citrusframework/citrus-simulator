import { expect, Page, test } from '@playwright/test';

import { messageHeaderJson, messageJson, scenarioActionJson, scenarioExecutionJson, testResultJson } from './helpers/entity-jsons';
import { EntityPageContentObject } from './helpers/helper-interfaces';
import { mockBackendResponse } from './helpers/helper-functions';

const exampleDate = '23 Aug 2024 08:25:31';
const entityPageContentMap: EntityPageContentObject[] = [
  {
    testName: 'should display table of messages and refresh button should work',
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
      { testId: 'messageEntityMessageId', expectedValue: '1' },
      { testId: 'messageEntityMessageDirection', expectedValue: 'INBOUND' },
      { testId: 'messageEntityMessagePayload', expectedValue: '<Default>Should trigger default scenario</Default>' },
      { testId: 'messageEntityMessageCitrusMessage', expectedValue: '5605967b-bfd6-42bb-ba3b-a2404d20783a' },
      { testId: 'messageEntityMessageCreatedDate', expectedValue: exampleDate },
      { testId: 'messageEntityMessageLastModified', expectedValue: exampleDate },
    ],
    testIdToBeVisible: ['filterOtherEntityButton'],
  },
  {
    testName: 'should display table of message headers and refresh button should work',
    apiUrl: '**/api/message-headers*',
    entityUrl: 'http://localhost:9000/message-header',
    contentJson: messageHeaderJson,
    locators: ['th :text("Name")', 'th :text("Value")'],
    testIdsAndExpectedValues: [
      { testId: 'messageHeaderEntityId', expectedValue: '13' },
      { testId: 'messageHeaderEntityName', expectedValue: 'Content-Type' },
      { testId: 'messageHeaderEntityValue', expectedValue: 'application/xml;charset=UTF-8' },
    ],
    testIdToBeVisible: [],
  },
  {
    testName: 'should display table of scenario executions and refresh button should work',
    apiUrl: '**/api/scenario-executions*',
    entityUrl: 'http://localhost:9000/scenario-execution',
    contentJson: scenarioExecutionJson,
    locators: ['th :text("Name")', 'th :text("Start Date")', 'th :text("End Date")', 'th :text("Status")', 'th :text("Error Message")'],
    testIdsAndExpectedValues: [
      { testId: 'scenarioExecutionEntityScenarioExecutionLink', expectedValue: '1' },
      { testId: 'scenarioExecutionEntityScenarioName', expectedValue: 'Default' },
      { testId: 'scenarioExecutionEntityStartDate', expectedValue: exampleDate },
      { testId: 'scenarioExecutionEntityEndDate', expectedValue: exampleDate },
      { testId: 'scenarioExecutionEntityStatus', expectedValue: 'FAILURE' },
      { testId: 'scenarioExecutionEntityTestResult', expectedValue: 'New Error' },
    ],
    testIdToBeVisible: [],
  },
  {
    testName: 'should display table of scenario actions and refresh button should work',
    apiUrl: '**/api/scenario-actions*',
    entityUrl: 'http://localhost:9000/scenario-action',
    contentJson: scenarioActionJson,
    locators: ['th :text("Name")', 'th :text("Start Date")', 'th :text("End Date")', 'th :text("Scenario Execution")'],
    testIdsAndExpectedValues: [
      { testId: 'scenarioActionEntitiesId', expectedValue: '1' },
      { testId: 'scenarioActionEntitiesName', expectedValue: 'http:receive-request' },
      { testId: 'scenarioActionEntitiesStartDate', expectedValue: exampleDate },
      { testId: 'scenarioActionEntitiesEndDate', expectedValue: exampleDate },
    ],
    testIdToBeVisible: [],
  },
  {
    testName: 'should display table of test results and refresh button should work',
    apiUrl: '**/api/test-results*',
    entityUrl: 'http://localhost:9000/test-result',
    contentJson: testResultJson,
    locators: [
      'th :text("Status")',
      'th :text("Test Name")',
      'th :text("Class Name")',
      'th :text("Error Message")',
      'th :text("Stack Trace")',
    ],
    testIdsAndExpectedValues: [
      { testId: 'testResultEntitiesId', expectedValue: '1' },
      { testId: 'testResultEntitiesStatus', expectedValue: 'FAILURE' },
      { testId: 'testResultEntitiesTestName', expectedValue: 'Scenario(Default)' },
      { testId: 'testResultEntitiesClassName', expectedValue: 'DefaultTestCase' },
      { testId: 'testResultEntitiesErrorMessage', expectedValue: 'New Error' },
      { testId: 'testResultEntitiesStackTrace', expectedValue: 'New Stacktrace' },
      { testId: 'testResultEntitiesFailureType', expectedValue: '' },
      { testId: 'testResultEntitiesCreatedDate', expectedValue: exampleDate },
      { testId: 'testResultEntitiesLastModifiedDate', expectedValue: exampleDate },
    ],
    testIdToBeVisible: [],
  },
];

entityPageContentMap.forEach((contentObject: EntityPageContentObject) => {
  test(`${contentObject.testName}`, async ({ page }) => {
    await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

    await page.goto(contentObject.entityUrl);

    await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
    await checkEntityPageContentValueAndVisibility(page, contentObject);
    await checkIfRefreshButtonWorks(page, contentObject);
  });
});

test.describe('message entity table', () => {
  test('should show message headers when clicking button', async ({ page }) => {
    const contentObject = entityPageContentMap[0];
    await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

    await page.goto(contentObject.entityUrl);

    await page.getByTestId('filterOtherEntityButton').nth(0).click();
    await expect(page.getByTestId('filterValue')).toHaveText('messageId.in: 1');
    await expect(page).toHaveURL('http://localhost:9000/message-header?filter%5BmessageId.in%5D=1');
  });
});

test.describe('test results entity table', () => {
  test('should show test parameters when clicking on button', async ({ page }) => {
    const contentObject = entityPageContentMap[4];
    await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

    await page.goto(contentObject.entityUrl);

    await page.getByTestId('testParametersButton').click();
    await expect(page.getByTestId('filterValue')).toHaveText('testResultId.in: 1');
    await expect(page).toHaveURL('http://localhost:9000/test-parameter?filter%5BtestResultId.in%5D=1');
  });
});

const checkEntityPageContentValueAndVisibility = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  for (const locator of contentObject.locators) {
    await expect(page.locator(locator)).toHaveCount(1);
  }
  for (const testId of contentObject.testIdsAndExpectedValues) {
    await expect(page.getByTestId(testId.testId)).toHaveText(testId.expectedValue);
  }
  for (const testId of contentObject.testIdToBeVisible) {
    await expect(page.getByTestId(testId)).toBeVisible();
  }
};

const checkIfRefreshButtonWorks = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  contentObject.contentJson.pop();
  await mockBackendResponse(page, contentObject.apiUrl, contentObject.contentJson);

  await page.getByTestId('refreshListButton').click();

  await checkContentAfterRefresh(page, contentObject);
};

const checkContentAfterRefresh = async (page: Page, contentObject: EntityPageContentObject): Promise<void> => {
  for (const locator of contentObject.locators) {
    await expect(page.locator(locator)).toHaveCount(0);
  }
  for (const testId of contentObject.testIdsAndExpectedValues) {
    await expect(page.getByTestId(testId.testId)).toBeHidden();
  }
  for (const testId of contentObject.testIdToBeVisible) {
    await expect(page.getByTestId(testId)).toBeHidden();
  }
};
