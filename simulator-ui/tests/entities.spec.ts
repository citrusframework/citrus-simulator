import {expect, test} from "@playwright/test";
import {mockBackendResponse} from "./helper-functions";


const entityPageContentMap = [
  {
    apiUrl: '...' /* TODO:die URLs der entsprechenden endpoints einfügen*/,
    entityUrl: 'http://localhost:9000/message',
    contentJson: {/* TODO: den Inhalt der Jsons befüllen entsprechend was gesendet wird*/}
  },
  {apiUrl: '...', entityUrl: 'http://localhost:9000/message-header', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-execution', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-action', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-parameter', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/test-result', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/test-parameter', contentJson: {}}
]

test('should test if content is displayed for every entity page', async ({page}) => {
  for (const entity of entityPageContentMap) {
    await mockBackendResponse(page, entity.apiUrl, entity.contentJson)
    await page.goto(entity.entityUrl);

    // TODO: Inhalt der Tabellen prüfen --> evnetuell Map ergänzen mit Testselektoren.
  }
})

test('should display table of messages', async ({page}) => {
  await page.route('**/api/messages*', async route => {
    const scenarioExecutionJson = [
        {
          "messageId": 1,
          "direction": "INBOUND",
          "payload": "<Default>Should trigger default scenario</Default>",
          "citrusMessageId": "5605967b-bfd6-42bb-ba3b-a2404d20783a",
          "headers": [
            {
              "headerId": 13,
              "name": "Content-Type",
              "value": "application/xml;charset=UTF-8",
              "createdDate": "2024-08-23T08:25:31.224400Z",
              "lastModifiedDate": "2024-08-23T08:25:31.224400Z"
            },
            {
              "headerId": 11,
              "name": "accept",
              "value": "text/plain, application/json, application/*+json, */*",
              "createdDate": "2024-08-23T08:25:31.224400Z",
              "lastModifiedDate": "2024-08-23T08:25:31.224400Z"
            },
          ],
          "createdDate": "2024-08-23T08:25:31.223402Z",
          "lastModifiedDate": "2024-08-23T08:25:31.223402Z"
        }
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/message/');

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await expect(page.locator('th :text("Direction")')).toHaveCount(1);
  await expect(page.locator('th :text("Payload")')).toHaveCount(1);
  await expect(page.locator('th :text("Citrus Message Id")')).toHaveCount(1);
  await expect(page.locator('th :text("Scenario Execution")')).toHaveCount(1);
  await expect(page.locator('th :text("Created Date")')).toHaveCount(1);
  await expect(page.locator('th :text("Last Modified Date")')).toHaveCount(1);
  await expect(page.getByTestId('messageEntityMessageId')).toHaveText('1')
  await expect(page.getByTestId('messageEntityMessageDirection')).toHaveText('INBOUND')
  await expect(page.getByTestId('messageEntityMessagePayload')).toHaveText('<Default>Should trigger default scenario</Default>')
  await expect(page.getByTestId('messageEntityMessageCitrusMessage')).toHaveText('5605967b-bfd6-42bb-ba3b-a2404d20783a')
  await expect(page.getByTestId('messageEntityMessageCreatedDate')).toHaveText('23 Aug 2024 08:25:31')
  await expect(page.getByTestId('messageEntityMessageLastModified')).toHaveText('23 Aug 2024 08:25:31')
  await expect(page.getByTestId('filterOtherEntityButton')).toBeVisible();
})

test('should display table of message headers', async ({page}) => {
  await page.route('**/api/message-headers*', async route => {
    const scenarioExecutionJson = [
        {
          "headerId": 1,
          "name": "contentType",
          "value": "application/xml;charset=UTF-8",
          "createdDate": "2024-08-23T08:25:31.224400Z",
          "lastModifiedDate": "2024-08-23T08:25:31.224400Z"
        },
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/message-header/');

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await expect(page.locator('th :text("Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Value")')).toHaveCount(1);
  await expect(page.getByTestId('messageHeaderEntityId')).toHaveText('1')
  await expect(page.getByTestId('messageHeaderEntityName')).toHaveText('contentType')
  await expect(page.getByTestId('messageHeaderEntityValue')).toHaveText('application/xml;charset=UTF-8')
})

test('should display table of scenario executions', async ({page}) => {
  await page.route('**/api/scenario-executions*', async route => {
    const scenarioExecutionJson = [
      {
        "executionId": 1,
        "startDate": "2024-08-23T08:25:30.950455Z",
        "endDate": "2024-08-23T08:25:31.386924Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 1,
          "status": "FAILURE",
          "testName": "Scenario(Default)",
          "className": "DefaultTestCase",
          "testParameters": null,
          "errorMessage": "New Error",
          "stackTrace": null,
          "failureType": null,
          "createdDate": "2024-08-23T08:25:31.372931Z",
          "lastModifiedDate": "2024-08-23T08:25:31.373926Z"
        },
        "scenarioParameters": [],
        "scenarioActions": [],
        "scenarioMessages": []
      },
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/scenario-execution/');

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await expect(page.locator('th :text("Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Start Date")')).toHaveCount(1);
  await expect(page.locator('th :text("End Date")')).toHaveCount(1);
  await expect(page.locator('th :text("Status")')).toHaveCount(1);
  await expect(page.locator('th :text("Error Message")')).toHaveCount(1);
  await expect(page.getByTestId('scenarioExecutionEntityScenarioExecutionLink')).toHaveText('1')
  await expect(page.getByTestId('scenarioExecutionEntityScenarioName')).toHaveText('Default')
  await expect(page.getByTestId('scenarioExecutionEntityStartDate')).toHaveText('23 Aug 2024 08:25:30')
  await expect(page.getByTestId('scenarioExecutionEntityEndDate')).toHaveText('23 Aug 2024 08:25:31')
  await expect(page.getByTestId('scenarioExecutionEntityStatus')).toHaveText('FAILURE')
  await expect(page.getByTestId('scenarioExecutionEntityTestResult')).toHaveText('New Error')
})

test('should display table of scenario actions', async ({page}) => {
  await page.route('**/api/scenario-actions*', async route => {
    const scenarioExecutionJson = [
      {
        "actionId": 1,
        "name": "http:receive-request",
        "startDate": "2024-08-23T08:25:31.201406Z",
        "endDate": "2024-08-23T08:25:31.327926Z"
      },
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/scenario-action/');

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await expect(page.locator('th :text("Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Start Date")')).toHaveCount(1);
  await expect(page.locator('th :text("End Date")')).toHaveCount(1);
  await expect(page.locator('th :text("Scenario Execution")')).toHaveCount(1);
  await expect(page.getByTestId('scenarioActionEntitiesId')).toHaveText('1')
  await expect(page.getByTestId('scenarioActionEntitiesName')).toHaveText('http:receive-request')
  await expect(page.getByTestId('scenarioActionEntitiesStartDate')).toHaveText('23 Aug 2024 08:25:31')
  await expect(page.getByTestId('scenarioActionEntitiesEndDate')).toHaveText('23 Aug 2024 08:25:31')
})

test('should display table of test results', async ({page}) => {
  await page.route('**/api/test-results*', async route => {
    const scenarioExecutionJson = [
      {
        "id": 1,
        "status": "FAILURE",
        "testName": "Scenario(Default)",
        "className": "DefaultTestCase",
        "testParameters": [],
        "errorMessage": "New Error",
        "stackTrace": "New Stacktrace",
        "failureType": null,
        "createdDate": "2024-08-23T08:25:31.372931Z",
        "lastModifiedDate": "2024-08-23T08:25:31.373926Z"
      },
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/test-result/');

  await expect(page.locator('th :text("ID")').nth(0)).toHaveCount(1);
  await expect(page.locator('th :text("Status")')).toHaveCount(1);
  await expect(page.locator('th :text("Test Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Class Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Error Message")')).toHaveCount(1);
  await expect(page.locator('th :text("Stack Trace")')).toHaveCount(1);
  await expect(page.getByTestId('testResultEntitiesId')).toHaveText('1')
  await expect(page.getByTestId('testResultEntitiesStatus')).toHaveText('FAILURE')
  await expect(page.getByTestId('testResultEntitiesTestName')).toHaveText('Scenario(Default)')
  await expect(page.getByTestId('testResultEntitiesClassName')).toHaveText('DefaultTestCase')
  await expect(page.getByTestId('testResultEntitiesErrorMessage')).toHaveText('New Error')
  await expect(page.getByTestId('testResultEntitiesStackTrace')).toHaveText('New Stacktrace')
  await expect(page.getByTestId('testResultEntitiesFailureType')).toHaveText('')
  await expect(page.getByTestId('testResultEntitiesCreatedDate')).toHaveText('23 Aug 2024 08:25:31')
  await expect(page.getByTestId('testResultEntitiesLastModifiedDate')).toHaveText('23 Aug 2024 08:25:31')
})
