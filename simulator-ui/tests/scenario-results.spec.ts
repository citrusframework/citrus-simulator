import {expect, Locator, test} from "@playwright/test";

test('should display input form', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.getByTestId('scenarioExecutionFilter/pageSize')).toBeVisible();
  await expect(page.locator('#pageSize')).toHaveValue('10');
  await expect(page.getByTestId('scenarioExecutionFilter/scenarioName')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFilter/status')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFilter/fromDate')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFilter/toDate')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFilter/messageHeaders')).toBeVisible();
});

test('should filter with input form', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');


  await page.route('**/api/scenario-executions?page=0&size=10&sort=executionId,asc', async route => {
    const scenarioExecutionsJson = [
      {
        "executionId": 630650,
        "startDate": "2024-06-20T10:05:42.239174Z",
        "endDate": "2024-06-20T10:05:42.608135Z",
        "scenarioName": "PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}",
        "testResult": {
          "id": 630101,
          "status": "SUCCESS",
          "testName": "PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}",
          "className": "DefaultTestCase",
          "testParameters": null,
          "errorMessage": null,
          "stackTrace": null,
          "failureType": null,
          "createdDate": "2024-06-20T08:05:42.603448Z",
          "lastModifiedDate": "2024-06-20T08:05:42.603448Z"
        },
        "scenarioParameters": [],
        "scenarioActions": [
          {
            "actionId": 3150466,
            "name": "AbstractOperationSimulatorScenario$$Lambda/0x00007feea178ae98",
            "startDate": "2024-06-20T10:05:42.253636Z",
            "endDate": "2024-06-20T10:05:42.270870Z"
          },
          {
            "actionId": 3150467,
            "name": "http:receive-request",
            "startDate": "2024-06-20T10:05:42.283446Z",
            "endDate": "2024-06-20T10:05:42.415375Z"
          },
          {
            "actionId": 3150468,
            "name": "MatchRequestAction",
            "startDate": "2024-06-20T10:05:42.427437Z",
            "endDate": "2024-06-20T10:05:42.482621Z"
          },
          {
            "actionId": 3150469,
            "name": "GetTestContextAction",
            "startDate": "2024-06-20T10:05:42.495890Z",
            "endDate": "2024-06-20T10:05:42.513154Z"
          },
          {
            "actionId": 3150470,
            "name": "http:send-response",
            "startDate": "2024-06-20T10:05:42.532637Z",
            "endDate": "2024-06-20T10:05:42.592635Z"
          }
        ],
        "scenarioMessages": [
          {
            "messageId": 1260915,
            "direction": "INBOUND",
            "payload": "{\n                            \"ids\": [1,2,3]\n                            }",
            "citrusMessageId": "23be1a00-7ce4-4387-8d1a-46e4b36d440d",
            "headers": null,
            "createdDate": "2024-06-20T08:05:42.299317Z",
            "lastModifiedDate": "2024-06-20T08:05:42.299317Z"
          },
          {
            "messageId": 1260916,
            "direction": "OUTBOUND",
            "payload": "{\n            \"id1\": \"1\",\n            \"id2\": \"2\",\n            \"id3\": \"3\",\n            \"q1\": \"q1Value\",\n            \"q2\": \"q2Value\",\n            \"h1\": \"h1Value\",\n            \"h2\": \"h2Value\",\n            \"body\": {\"ids\":[1,2,3]}\n}",
            "citrusMessageId": "864e1880-9d16-4907-b7f0-48c52cec780f",
            "headers": null,
            "createdDate": "2024-06-20T08:05:42.549112Z",
            "lastModifiedDate": "2024-06-20T08:05:42.549112Z"
          }
        ]
      },
      {
        "executionId": 635675,
        "startDate": "2024-06-20T10:43:15.172667Z",
        "endDate": "2024-06-20T10:43:15.493745Z",
        "scenarioName": "PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}",
        "testResult": {
          "id": 635128,
          "status": "SUCCESS",
          "testName": "PUT_/services/rest/RestRespondWithRequestValuesTest/{id1}/{id2}/{id3}",
          "className": "DefaultTestCase",
          "testParameters": null,
          "errorMessage": null,
          "stackTrace": null,
          "failureType": null,
          "createdDate": "2024-06-20T08:43:15.489557Z",
          "lastModifiedDate": "2024-06-20T08:43:15.489557Z"
        },
        "scenarioParameters": [],
        "scenarioActions": [
          {
            "actionId": 3175577,
            "name": "AbstractOperationSimulatorScenario$$Lambda/0x00007f8a4178c748",
            "startDate": "2024-06-20T10:43:15.187104Z",
            "endDate": "2024-06-20T10:43:15.204815Z"
          },
          {
            "actionId": 3175579,
            "name": "http:receive-request",
            "startDate": "2024-06-20T10:43:15.217532Z",
            "endDate": "2024-06-20T10:43:15.300350Z"
          },
          {
            "actionId": 3175593,
            "name": "MatchRequestAction",
            "startDate": "2024-06-20T10:43:15.313720Z",
            "endDate": "2024-06-20T10:43:15.376332Z"
          },
          {
            "actionId": 3175607,
            "name": "GetTestContextAction",
            "startDate": "2024-06-20T10:43:15.389336Z",
            "endDate": "2024-06-20T10:43:15.404897Z"
          },
          {
            "actionId": 3175615,
            "name": "http:send-response",
            "startDate": "2024-06-20T10:43:15.423314Z",
            "endDate": "2024-06-20T10:43:15.473550Z"
          }
        ],
        "scenarioMessages": [
          {
            "messageId": 1270958,
            "direction": "INBOUND",
            "payload": "{\n                            \"ids\": [1,2,3]\n                            }",
            "citrusMessageId": "dfb36c8f-ee48-4283-ae51-efc302014004",
            "headers": null,
            "createdDate": "2024-06-20T08:43:15.231392Z",
            "lastModifiedDate": "2024-06-20T08:43:15.231392Z"
          },
          {
            "messageId": 1270971,
            "direction": "OUTBOUND",
            "payload": "{\n            \"id1\": \"1\",\n            \"id2\": \"2\",\n            \"id3\": \"3\",\n            \"q1\": \"q1Value\",\n            \"q2\": \"q2Value\",\n            \"h1\": \"h1Value\",\n            \"h2\": \"h2Value\",\n            \"body\": {\"ids\":[1,2,3]}\n}",
            "citrusMessageId": "0eedfdec-82eb-446c-b38c-2cfe533e372e",
            "headers": null,
            "createdDate": "2024-06-20T08:43:15.437484Z",
            "lastModifiedDate": "2024-06-20T08:43:15.437484Z"
          }
        ]
      },
    ]
    await route.fulfill({json: scenarioExecutionsJson});
  });

  await page.route('**/api/scenario-executions?page=1&size=10&sort=executionId,asc&filter%5BstartDate.greaterThanOrEqual%5D=2024-07-21T10:05:31.000Z&filter%5BendDate.lessThanOrEqual%5D=2024-07-22T03:02:07.000Z', async route => {
    await route.fulfill({});
  });
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilter/scenarioName').fill('Test Scenario');
  await page.getByTestId('scenarioExecutionFilter/status').selectOption('Failure');
  await fillDatePickerField(page.getByTestId('scenarioExecutionFilter/fromDate'), '21072024', '120531')
  await fillDatePickerField(page.getByTestId('scenarioExecutionFilter/toDate'), '22072024', '052007')
  await page.getByTestId('scenarioExecutionFilter/messageHeaders').fill('Test Headers');
});

const fillDatePickerField = async (dateField: Locator, date: string, time: string): Promise<any> => {
  await dateField.click();
  await dateField.pressSequentially(date);
  await dateField.press('Tab');
  await dateField.pressSequentially(time);
}

test('should display table headers for scenario executions', async ({page}) => {
  await page.route('**/api/scenario-executions*', async route => {
    const scenarioExecutionJson = [
      {
        "executionId": 752603,
        "startDate": "2024-06-27T10:59:03.872021Z",
        "endDate": "2024-06-27T11:05:21.168103Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 752026,
          "status": "FAILURE",
          "errorMessage": "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
          "createdDate": "2024-06-27T08:59:04.159168Z",
        },
      }
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.locator('th :text("ID")')).toHaveCount(1);
  await expect(page.locator('th :text("Scenario Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Start Date")')).toHaveCount(1);
  await expect(page.locator('th :text("End Date")')).toHaveCount(1);
  await expect(page.locator('th :text("Status")')).toHaveCount(1);
  await expect(page.locator('th :text("Error Message")')).toHaveCount(1);
});

test('should display table row for scenario executions', async ({page}) => {
  await page.route('**/api/scenario-executions*', async route => {
    const scenarioExecutionJson = [
      {
        "executionId": 752603,
        "startDate": "2024-06-27T10:59:03.872021Z",
        "endDate": "2024-06-27T11:05:21.168103Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 752026,
          "status": "FAILURE",
          "errorMessage": "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
          "createdDate": "2024-06-27T08:59:04.159168Z",
        },
      }
    ];
    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.locator('tr :text("752603")')).toHaveCount(1);
  await expect(page.locator('tr :text("Default")')).toHaveCount(1);
  await expect(page.locator('tr :text("27 Jun 2024 10:59:03")')).toHaveCount(1);
  await expect(page.locator('tr :text("27 Jun 2024 11:05:21")')).toHaveCount(1);
  await expect(page.locator('tr :text("FAILURE")')).toHaveCount(1);
  await expect(page.locator('tr :text("Unable to validate because message store is not of type \'CorrelatedMessageProvider\'! Check your configuration and register a suitable message store.")')).toHaveCount(1);
  await expect(page.locator('tr :text("Scenario Actions")')).toHaveCount(1);
  await expect(page.locator('tr :text("Scenario Messages")')).toHaveCount(1);
  await expect(page.locator('tr :text("Scenario Parameters")')).toHaveCount(1);
  await expect(page.locator('tr :text("View")')).toHaveCount(1);
});

test('should display help dialog after clicking button', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilter/dialogButton').click();
  await expect(page.getByTestId(('scenarioExecutionFilter/helpDialog'))).toBeVisible();
});

test('should display filter message header popup after clicking button', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilter/headerFilterButton').click();
  await expect(page.getByTestId(('scenarioExecutionFilter/filterMessageHeaderForm'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/headerName'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/headerValue'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/valueComparator'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/valueType'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/addAnotherButton'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/cancelButton'))).toBeVisible();
  await expect(page.getByTestId(('scenarioExecutionFilter/applyFilterButton'))).toBeVisible();
});



