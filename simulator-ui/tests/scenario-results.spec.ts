import { expect, Locator, test } from '@playwright/test';

import { mockBackendResponse } from './helpers/helper-functions';
import {
  messageHeaderJson,
  messageJson,
  scenarioActionJson,
  scenarioExecutionJsonWithDetails,
  scenarioExecutionJsonWithoutDetails,
  twoScenarioExecutions,
} from './helpers/scenario-results-jsons';

test('should display input form', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.getByTestId('itemsPerPageSelect')).toBeVisible();
  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue('10');
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionFromDateInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionToDateInput')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toBeVisible();
});

test('should filter with input form', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');
  await mockBackendResponse(page, '**/api/scenario-executions?page=0&size=10&sort=executionId,asc', twoScenarioExecutions);
  await mockBackendResponse(
    page,
    '**/api/scenario-executions?page=1&size=10&sort=executionId,asc&filter%5BstartDate.greaterThanOrEqual%5D=2024-07-21T10:05:31.000Z&filter%5BendDate.lessThanOrEqual%5D=2024-07-22T03:02:07.000Z',
    [],
  );

  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilterInput').fill('Test Scenario');
  await page.getByTestId('scenarioExecutionStatusInSelect').selectOption('Failure');
  await fillDatePickerField(page.getByTestId('scenarioExecutionFromDateInput'), '21072024', '120531');
  await fillDatePickerField(page.getByTestId('scenarioExecutionToDateInput'), '22072024', '052007');
  await page.getByTestId('scenarioExecutionHeaderFilterInput').fill('Test Headers');
});

const fillDatePickerField = async (dateField: Locator, date: string, time: string): Promise<any> => {
  await dateField.click();
  await dateField.pressSequentially(date);
  await dateField.press('Tab');
  await dateField.pressSequentially(time);
};

test('should display table headers for scenario executions', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions*', scenarioExecutionJsonWithoutDetails);

  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.locator('th :text("ID")')).toHaveCount(1);
  await expect(page.locator('th :text("Name")')).toHaveCount(1);
  await expect(page.locator('th :text("Start Date")')).toHaveCount(1);
  await expect(page.locator('th :text("End Date")')).toHaveCount(1);
  await expect(page.locator('th :text("Status")')).toHaveCount(1);
  await expect(page.locator('th :text("Error Message")')).toHaveCount(1);
});

test('should display table row for scenario executions', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions*', scenarioExecutionJsonWithoutDetails);

  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.locator('tr :text("752603")')).toHaveCount(1);
  await expect(page.locator('tr :text("Default")')).toHaveCount(1);
  await expect(page.locator('tr :text("27 Jun 2024 10:59:03")')).toHaveCount(1);
  await expect(page.locator('tr :text("27 Jun 2024 11:05:21")')).toHaveCount(1);
  await expect(page.locator('tr :text("FAILURE")')).toHaveCount(1);
  await expect(
    page.locator(
      'tr :text("Unable to validate because message store is not of type \'CorrelatedMessageProvider\'! Check your configuration and register a suitable message store.")',
    ),
  ).toHaveCount(1);
  await expect(page.locator('tr :text("Actions")')).toHaveCount(1);
  await expect(page.locator('tr :text("Messages")')).toHaveCount(1);
  await expect(page.locator('tr :text("Parameters")')).toHaveCount(1);
});

test('should display help dialog after clicking button', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenHelpButton').click();
  await expect(page.getByTestId('helpDialog')).toBeVisible();
});

test('should display filter message header popup after clicking button', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await expect(page.getByTestId('headerFilterInput')).toBeVisible();
  await expect(page.getByTestId('headerFilterTypeSelect')).toBeVisible();
  await expect(page.getByTestId('headerFilterSelect')).toBeVisible();
  await expect(page.getByTestId('headerValueInput')).toBeVisible();
  await expect(page.getByTestId('addHeaderFilterButton')).toBeVisible();
  await expect(page.getByTestId('cancelButton')).toBeVisible();
  await expect(page.getByTestId('applyHeaderFilterButton')).toBeVisible();
});

test('should filter message headers with header name and header value', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId('headerFilterInput').fill('HeaderName1');
  await page.getByTestId('headerValueInput').fill('HeaderValue1');
  await page.getByTestId('addHeaderFilterButton').click();
  await page.locator('#header-1').fill('HeaderName2');
  await page.locator('#header-1-value-comparator').selectOption('contains');
  await page.locator('#header-1-value').fill('HeaderValue2');
  await page.getByTestId('applyHeaderFilterButton').click();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toHaveValue('HeaderName1=HeaderValue1; HeaderName2~HeaderValue2');
});

test('should delete second message header filter', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId('addHeaderFilterButton').click();
  await expect(page.getByTestId('removeHeaderFilterButton').nth(0)).toBeDisabled();
  await expect(page.getByTestId('removeHeaderFilterButton').nth(1)).toBeEnabled();
  await page.getByTestId('removeHeaderFilterButton').nth(1).click();
  await expect(page.locator('#header-1')).toHaveCount(0);
  await expect(page.locator('#header-1-value-comparator')).toHaveCount(0);
  await expect(page.locator('#header-1-value')).toHaveCount(0);
});

test('should clear all filters with button', async ({ page }) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByTestId('scenarioExecutionFilterInput').fill('Test Scenario');
  await page.getByTestId('scenarioExecutionStatusInSelect').selectOption('Failure');
  await fillDatePickerField(page.getByTestId('scenarioExecutionFromDateInput'), '21072024', '120531');
  await fillDatePickerField(page.getByTestId('scenarioExecutionToDateInput'), '22072024', '052007');
  await page.getByTestId('scenarioExecutionHeaderFilterInput').fill('Test Headers');

  await page.getByTestId('scenarioExecutionOpenFilterButton').click();
  await page.getByTestId('headerFilterInput').fill('HeaderName1');
  await page.getByTestId('headerValueInput').fill('HeaderValue1');
  await page.getByTestId('addHeaderFilterButton').click();
  await page.locator('#header-1').fill('HeaderName2');
  await page.locator('#header-1-value-comparator').selectOption('contains');
  await page.locator('#header-1-value').fill('HeaderValue2');
  await page.getByTestId('applyHeaderFilterButton').click();
  await page.getByTestId('clearScenarioExecutionsFilterButton').click();

  await expect(page.getByTestId('scenarioExecutionFilterInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue('');
  await expect(page.getByTestId('scenarioExecutionFromDateInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionToDateInput')).toBeEmpty();
  await expect(page.getByTestId('scenarioExecutionHeaderFilterInput')).toBeEmpty();
});

test('should display detail view of scenario execution', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions*', scenarioExecutionJsonWithoutDetails);
  await mockBackendResponse(page, '**/api/scenario-executions/752603', scenarioExecutionJsonWithDetails);

  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByRole('link', { name: '752603' }).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await expect(page.getByTestId('scenarioExecutionId')).toHaveText('752603');
  await expect(page.getByTestId('scenarioExecutionStartDate')).toHaveText('27 Jun 2024 10:59:03');
  await expect(page.getByTestId('scenarioExecutionEndDate')).toHaveText('27 Jun 2024 11:05:21');
  await expect(page.getByTestId('scenarioExecutionName')).toHaveText('Default');
  await expect(page.getByTestId('scenarioExecutionStatus')).toHaveText('FAILURE');
  await expect(page.getByTestId('scenarioExecutionErrorMessage')).toHaveText(
    "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
  );
  await page.getByTestId('openStackTraceButton').click();
  await expect(page.getByTestId('scenarioExecutionStackTrace')).toHaveText(
    'org.citrusframework.exceptions.CitrusRuntimeException: It is the courage to continue that counts. at org.citrusframework.actions.FailAction.doExecute(FailAction.java:43) at org.citrusframework.actions.AbstractTestAction.execute(AbstractTestAction.java:59) at org.citrusframework.DefaultTestCase.executeAction(DefaultTestCase.java:190) at org.citrusframework.DefaultTestCaseRunner.run(DefaultTestCaseRunner.java:145) at org.citrusframework.simulator.scenario.ScenarioRunner.run(ScenarioRunner.java:79) at org.citrusframework.TestActionRunner.$(TestActionRunner.java:51) at org.citrusframework.simulator.sample.scenario.FailScenario.run(FailScenario.java:49) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.createAndRunScenarioRunner(DefaultScenarioExecutorService.java:147) at org.citrusframework.simulator.service.runner.DefaultScenarioExecutorService.startScenario(DefaultScenarioExecutorService.java:116) at org.citrusframework.simulator.service.runner.AsyncScenarioExecutorService.lambda$startScenarioAsync$0(AsyncScenarioExecutorService.java:126) at java.base',
  );
});

test('should display detail view of message', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions?page=0&size=10&sort=executionId,asc', scenarioExecutionJsonWithoutDetails);
  await mockBackendResponse(page, '**/api/scenario-executions/752603', scenarioExecutionJsonWithDetails);
  await mockBackendResponse(page, '**/api/messages/20', messageJson);

  await page.goto('http://localhost:9000/scenario-result/');
  await page.getByRole('link', { name: '752603' }).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await page.getByTestId('scenarioMessagesEntityMessageLink').click();

  await expect(page).toHaveURL('http://localhost:9000/message/20/view');
  await expect(page.getByTestId('messageDetailId')).toHaveText('20');
  await expect(page.getByTestId('messageDetailDirection')).toHaveText('INBOUND');
  await expect(page.getByTestId('messageDetailPayload')).toBeEmpty();
  await expect(page.getByTestId('messageDetailCitrusMessageId')).toHaveText('c65cdf92-7075-44d6-b0f2-42a556d12f80');
  await expect(page.getByTestId('messageDetailCreatedDate')).toHaveText('22 Aug 2024 08:38:56');
  await expect(page.getByTestId('messageDetailLastModifiedDate')).toHaveText('22 Aug 2024 08:38:56');

  await expect(page.locator('tr :text("214")')).toHaveCount(1);
  await expect(page.locator('tr :text("accept")')).toHaveCount(1);
  await expect(page.locator('tr :text("application/json")')).toHaveCount(1);
  await expect(page.locator('tr :text("22 Aug 2024 08:38:56")').nth(0)).toHaveCount(1);
  await expect(page.locator('tr :text("22 Aug 2024 08:38:56")').nth(1)).toHaveCount(1);

  await expect(page.locator('tr :text("207")')).toHaveCount(1);
  await expect(page.locator('tr :text("deflect-encoding")')).toHaveCount(1);
  await expect(page.locator('tr :text("gzip, x-gzip, deflate")')).toHaveCount(1);
  await expect(page.locator('tr :text("22 Aug 2024 08:38:56")').nth(2)).toHaveCount(1);
  await expect(page.locator('tr :text("22 Aug 2024 08:38:56")').nth(3)).toHaveCount(1);
});

test('should display detail view of message header', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions?page=0&size=10&sort=executionId,asc', scenarioExecutionJsonWithoutDetails);
  await mockBackendResponse(page, '**/api/scenario-executions/752603', scenarioExecutionJsonWithDetails);
  await mockBackendResponse(page, '**/api/messages/20', messageJson);
  await mockBackendResponse(page, '**/api/message-headers/214', messageHeaderJson);

  await page.goto('http://localhost:9000/scenario-result/');
  await page.getByRole('link', { name: '752603' }).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await page.getByTestId('scenarioMessagesEntityMessageLink').click();
  await expect(page).toHaveURL('http://localhost:9000/message/20/view');
  await page.getByTestId('messageHeaderEntityId').nth(0).click();
  await expect(page).toHaveURL('http://localhost:9000/message-header/214/view');
  await expect(page.getByTestId('messageHeaderDetailsId')).toHaveText('214');
  await expect(page.getByTestId('messageHeaderDetailsName')).toHaveText('accept');
  await expect(page.getByTestId('messageHeaderDetailsValue')).toHaveText('application/json');
  await expect(page.getByTestId('messageHeaderDetailsCreatedDate')).toHaveText('22 Aug 2024 08:38:56');
  await expect(page.getByTestId('messageHeaderDetailsLastModified')).toHaveText('22 Aug 2024 08:38:56');
});

test('should display detail view of scenario action', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenario-executions*', scenarioExecutionJsonWithoutDetails);
  await mockBackendResponse(page, '**/api/scenario-executions/752603', scenarioExecutionJsonWithDetails);
  await mockBackendResponse(page, '**/api/scenario-actions/29', scenarioActionJson);

  await page.goto('http://localhost:9000/scenario-result/');

  await page.getByRole('link', { name: '752603' }).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-execution/752603/view');
  await page.getByTestId('scenarioActionsEntityScenarioActionLink').nth(0).click();
  await expect(page).toHaveURL('http://localhost:9000/scenario-action/29/view');
  await expect(page.getByTestId('scenarioActionDetailsId')).toHaveText('29');
  await expect(page.getByTestId('scenarioActionDetailName')).toHaveText('http:receive-request');
  await expect(page.getByTestId('scenarioActionDetailsStartDate')).toHaveText('27 Jun 2024 15:10:03');
  await expect(page.getByTestId('scenarioActionDetailsEndDate')).toHaveText('15 Aug 2024 15:10:03');
});
