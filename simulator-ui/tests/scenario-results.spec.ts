import {expect, Locator, test} from "@playwright/test";

test('should display input form', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.getByTestId('itemsPerPageSelect')).toBeVisible();
  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue('10');
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFromDateInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionToDateInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toBeVisible();
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

  await page.getByTestId('scenarioExecutionFilterInput').fill('Test Scenario');
  await page.getByTestId('scenarioExecutionStatusInSelect').selectOption('Failure');
  await fillDatePickerField(page.getByTestId('scenarioExecutionFromDateInput'), '21072024', '120531')
  await fillDatePickerField(page.getByTestId('scenarioExecutionToDateInput'), '22072024', '052007')
  await page.getByTestId('scenarioExecutionHeaderFilterInput').fill('Test Headers');
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
  await expect(page.locator('th :text("Name")')).toHaveCount(1);
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
  await expect(page.locator('tr :text("Actions")')).toHaveCount(1);
  await expect(page.locator('tr :text("Messages")')).toHaveCount(1);
  await expect(page.locator('tr :text("Parameters")')).toHaveCount(1);
});

test('should display help dialog after clicking button', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenHelpButton').click();
  await expect(page.getByTestId(('helpDialog'))).toBeVisible();
});

test('should display filter message header popup after clicking button', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await expect(page.getByTestId(('headerFilterInput'))).toBeVisible();
  await expect(page.getByTestId(('headerFilterTypeSelect'))).toBeVisible();
  await expect(page.getByTestId(('headerFilterSelect'))).toBeVisible();
  await expect(page.getByTestId(('headerValueInput'))).toBeVisible();
  await expect(page.getByTestId(('addHeaderFilterButton'))).toBeVisible();
  await expect(page.getByTestId(('cancelButton'))).toBeVisible();
  await expect(page.getByTestId(('applyHeaderFilterButton'))).toBeVisible();
});

test('should filter message headers with header name and header value', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId(('headerFilterInput')).fill('HeaderName1');
  await page.getByTestId(('headerValueInput')).fill('HeaderValue1');
  await page.getByTestId(('addHeaderFilterButton')).click();
  await page.locator(('#header-1')).fill('HeaderName2');
  await page.locator(('#header-1-value-comparator')).selectOption('contains');
  await page.locator(('#header-1-value')).fill('HeaderValue2');
  await page.getByTestId(('applyHeaderFilterButton')).click();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toHaveValue('HeaderName1=HeaderValue1; HeaderName2~HeaderValue2');
});

test('should delete second message header filter', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId(('addHeaderFilterButton')).click();
  await expect(page.getByTestId(('removeHeaderFilterButton')).nth(0)).toBeDisabled();
  await expect(page.getByTestId(('removeHeaderFilterButton')).nth(1)).toBeEnabled();
  await page.getByTestId(('removeHeaderFilterButton')).nth(1).click();
  await expect(page.locator(('#header-1'))).toHaveCount(0);
  await expect(page.locator(('#header-1-value-comparator'))).toHaveCount(0);
  await expect(page.locator(('#header-1-value'))).toHaveCount(0);
});

test('should clear all filters', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilterInput').fill('Test Scenario');
  await page.getByTestId('scenarioExecutionStatusInSelect').selectOption('Failure');
  await fillDatePickerField(page.getByTestId('scenarioExecutionFromDateInput'), '21072024', '120531')
  await fillDatePickerField(page.getByTestId('scenarioExecutionToDateInput'), '22072024', '052007')
  await page.getByTestId('scenarioExecutionHeaderFilterInput').fill('Test Headers');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId(('headerFilterInput')).fill('HeaderName1');
  await page.getByTestId(('headerValueInput')).fill('HeaderValue1');
  await page.getByTestId(('addHeaderFilterButton')).click();
  await page.locator(('#header-1')).fill('HeaderName2');
  await page.locator(('#header-1-value-comparator')).selectOption('contains');
  await page.locator(('#header-1-value')).fill('HeaderValue2');
  await page.getByTestId(('applyHeaderFilterButton')).click();
  await page.getByTestId('clearScenarioExecutionsFilterButton').click();

  await expect(page.getByTestId('scenarioExecutionFilterInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue('');
  await expect(page.getByTestId('scenarioExecutionFromDateInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionToDateInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toBeEmpty();
});

test('should display detail view of scenario execution', async ({page}) => {
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

  await page.route('**/api/scenario-executions/752603', async route => {
    const scenarioExecutionJson =
      {
        "executionId": 752603,
        "startDate": "2024-06-27T10:59:03.872021Z",
        "endDate": "2024-06-27T11:05:21.168103Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 752026,
          "status": "FAILURE",
          "errorMessage": "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
          "stackTrace" : "org.citrusframework.exceptions.CitrusRuntimeException: It is the courage to continue that counts. at org.citrusframework.actions.FailAction.doExecute(FailAction.java:43) at org.citrusframework.actions.AbstractTestAction.execute(AbstractTestAction.java:59) at org.citrusframework.DefaultTestCase.executeAction(DefaultTestCase.java:190) at org.citrusframework.DefaultTestCaseRunner.run(DefaultTestCaseRunner.java:145) at org.citrusframework.simulator.scenario.ScenarioRunner.run(ScenarioRunner.java:79) at org.citrusframework.TestActionRunner.$(TestActionRunner.java:51) at org.citrusframework.simulator.sample.scenario.FailScenario.run(FailScenario.java:49) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.createAndRunScenarioRunner(DefaultScenarioExecutorService.java:147) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.startScenario(DefaultScenarioExecutorService.java:116) at org.citrusframework.simulator.service.runner.AsyncScenarioExecutorService.lambda$startScenarioAsync$0(AsyncScenarioExecutorService.java:126) at java.base",
          "createdDate": "2024-06-27T08:59:04.159168Z",
        },
        "scenarioParameters": [
          {
            "parameterId": 0,
            "name": "scenario parameter name",
            "controlType": "Control Type of parameter",
            "value": "scenario parameter value",
            "options": [
              {
                "key": "random key",
                "value": "random value"
              }
            ],
            "createdDate": "2024-06-27T09:27:02.286Z",
            "lastModifiedDate": "2024-06-27T09:27:02.286Z"
          },
        ],
        "scenarioActions": [
          {
            "actionId": 29,
            "name": "http:receive-request",
            "startDate": "2024-06-27T15:10:03.616968Z",
            "endDate": "2024-08-15T15:10:03.632568Z"
          },
          {
            "actionId": 30,
            "name": "echo",
            "startDate": "2024-06-27T15:10:03.632568Z",
            "endDate": "2024-06-27T15:10:03.648188Z"
          }
        ],
        "scenarioMessages": [
          {
            "messageId": 20,
            "direction": "INBOUND",
            "payload": "",
            "citrusMessageId": "0fa15e84-422f-4a61-a587-ea33d12e4b38",
            "headers": null,
            "createdDate": "2024-06-27T13:10:03.632568Z",
            "lastModifiedDate": "2024-06-27T13:10:03.632568Z"
          }
        ]

      };

    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByRole('link', {name: '752603'}).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await expect(page.getByTestId('scenarioExecutionId')).toHaveText('752603');
  await expect(page.getByTestId('scenarioExecutionStartDate')).toHaveText('27 Jun 2024 10:59:03');
  await expect(page.getByTestId('scenarioExecutionEndDate')).toHaveText('27 Jun 2024 11:05:21');
  await expect(page.getByTestId('scenarioExecutionName')).toHaveText('Default');
  await expect(page.getByTestId('scenarioExecutionStatus')).toHaveText('FAILURE');
  await expect(page.getByTestId('scenarioExecutionErrorMessage')).toHaveText('Unable to validate because message store is not of type \'CorrelatedMessageProvider\'! Check your configuration and register a suitable message store.');
  await page.getByTestId('openStackTraceButton').click();
  await expect(page.getByTestId('scenarioExecutionStackTrace')).toHaveText('org.citrusframework.exceptions.CitrusRuntimeException: It is the courage to continue that counts. at org.citrusframework.actions.FailAction.doExecute(FailAction.java:43) at org.citrusframework.actions.AbstractTestAction.execute(AbstractTestAction.java:59) at org.citrusframework.DefaultTestCase.executeAction(DefaultTestCase.java:190) at org.citrusframework.DefaultTestCaseRunner.run(DefaultTestCaseRunner.java:145) at org.citrusframework.simulator.scenario.ScenarioRunner.run(ScenarioRunner.java:79) at org.citrusframework.TestActionRunner.$(TestActionRunner.java:51) at org.citrusframework.simulator.sample.scenario.FailScenario.run(FailScenario.java:49) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.createAndRunScenarioRunner(DefaultScenarioExecutorService.java:147) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.startScenario(DefaultScenarioExecutorService.java:116) at org.citrusframework.simulator.service.runner.AsyncScenarioExecutorService.lambda$startScenarioAsync$0(AsyncScenarioExecutorService.java:126) at java.base');
});

test('should display detail view of message', async ({page}) => {
  await page.route('**/api/scenario-executions?page=0&size=10&sort=executionId,asc', async route => {
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

  await page.route('**/api/scenario-executions/752603', async route => {
    const scenarioExecutionJson =
      {
        "executionId": 752603,
        "startDate": "2024-06-27T10:59:03.872021Z",
        "endDate": "2024-06-27T11:05:21.168103Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 752026,
          "status": "FAILURE",
          "errorMessage": "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
          "stackTrace" : "org.citrusframework.exceptions.CitrusRuntimeException: It is the courage to continue that counts. at org.citrusframework.actions.FailAction.doExecute(FailAction.java:43) at org.citrusframework.actions.AbstractTestAction.execute(AbstractTestAction.java:59) at org.citrusframework.DefaultTestCase.executeAction(DefaultTestCase.java:190) at org.citrusframework.DefaultTestCaseRunner.run(DefaultTestCaseRunner.java:145) at org.citrusframework.simulator.scenario.ScenarioRunner.run(ScenarioRunner.java:79) at org.citrusframework.TestActionRunner.$(TestActionRunner.java:51) at org.citrusframework.simulator.sample.scenario.FailScenario.run(FailScenario.java:49) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.createAndRunScenarioRunner(DefaultScenarioExecutorService.java:147) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.startScenario(DefaultScenarioExecutorService.java:116) at org.citrusframework.simulator.service.runner.AsyncScenarioExecutorService.lambda$startScenarioAsync$0(AsyncScenarioExecutorService.java:126) at java.base",
          "createdDate": "2024-06-27T08:59:04.159168Z",
        },
        "scenarioParameters": [
          {
            "parameterId": 0,
            "name": "scenario parameter name",
            "controlType": "Control Type of parameter",
            "value": "scenario parameter value",
            "options": [
              {
                "key": "random key",
                "value": "random value"
              }
            ],
            "createdDate": "2024-06-27T09:27:02.286Z",
            "lastModifiedDate": "2024-06-27T09:27:02.286Z"
          },
        ],
        "scenarioActions": [
          {
            "actionId": 29,
            "name": "http:receive-request",
            "startDate": "2024-06-27T15:10:03.616968Z",
            "endDate": "2024-08-15T15:10:03.632568Z"
          },
          {
            "actionId": 30,
            "name": "echo",
            "startDate": "2024-06-27T15:10:03.632568Z",
            "endDate": "2024-06-27T15:10:03.648188Z"
          }
        ],
        "scenarioMessages": [
          {
            "messageId": 20,
            "direction": "INBOUND",
            "payload": "",
            "citrusMessageId": "0fa15e84-422f-4a61-a587-ea33d12e4b38",
            "headers": null,
            "createdDate": "2024-06-27T13:10:03.632568Z",
            "lastModifiedDate": "2024-06-27T13:10:03.632568Z"
          }
        ]

      };

    await route.fulfill({json: scenarioExecutionJson});
  });

  await page.route('**/api/messages/20', async route => {
    const scenarioExecutionJson =
      {
        "messageId": 20,
        "direction": "INBOUND",
        "payload": "",
        "citrusMessageId": "c65cdf92-7075-44d6-b0f2-42a556d12f80",
        "headers": [
          {
            "headerId": 214,
            "name": "accept",
            "value": "application/json",
            "createdDate": "2024-08-22T08:38:56.708514Z",
            "lastModifiedDate": "2024-08-22T08:38:56.708514Z"
          },
          {
            "headerId": 207,
            "name": "accept-encoding",
            "value": "gzip, x-gzip, deflate",
            "createdDate": "2024-08-22T08:38:56.708514Z",
            "lastModifiedDate": "2024-08-22T08:38:56.708514Z"
          },
        ],
        "createdDate": "2024-08-22T08:38:56.632568Z",
        "lastModifiedDate": "2024-08-22T08:38:56.632568Z"
      };
    await route.fulfill({json: scenarioExecutionJson});
  });



  await page.goto('http://localhost:9000/scenario-result/');
  await page.getByRole('link', {name: '752603'}).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await page.getByTestId('scenarioMessagesEntityMessageLink').click();

  await expect(page).toHaveURL('http://localhost:9000/message/20/view');
  await expect(page.getByTestId('messageDetailId')).toHaveText('20');
  await expect(page.getByTestId('messageDetailDirection')).toHaveText('INBOUND');
  await expect(page.getByTestId('messageDetailPayload')).toBeEmpty();
  await expect(page.getByTestId('messageDetailCitrusMessageId')).toHaveText('c65cdf92-7075-44d6-b0f2-42a556d12f80');
  await expect(page.getByTestId('messageDetailCreatedDate')).toHaveText('22 Aug 2024 08:38:56');
  await expect(page.getByTestId('messageDetailLastModifiedDate')).toHaveText('22 Aug 2024 08:38:56');
});
