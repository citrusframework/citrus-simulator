import {test, expect} from '@playwright/test';

test('should have title', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page).toHaveTitle(/Citrus Simulator/);
});

test('should have refresh list, and reset button', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByText('Refresh List')).toBeVisible();
  await expect(page.getByText('Reset')).toBeVisible();
});

test('should have total, successful, failed tabs', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": 90, "failed": 10, "total": 100};
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/');

  await expect(page.getByText('Total:')).toBeVisible();
  await expect(page.getByText('Successful:')).toBeVisible();
  await expect(page.getByText('Failed:')).toBeVisible();
});

test('total, successful, failed tabs should display simulations count', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": 170, "failed": 30, "total": 200};
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/');

  await expect(page.getByText('200')).toBeVisible();
  await expect(page.getByText('170')).toBeVisible();
  await expect(page.getByText('30')).toBeVisible();
});

test('total, successful, failed tabs should display percentage in simulations count', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": 746039, "failed": 490, "total": 746529};
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/');

  await expect(page.getByText('100 %')).toBeVisible();
  await expect(page.getByText('99.93 %')).toBeVisible();
  await expect(page.getByText('0.07 %')).toBeVisible();
});

test('should display footer', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByText('©2023 to the original author or authors.')).toBeVisible();
  await expect(page.getByText('Citrusframework/Simulator')).toBeVisible();
  await expect(page.getByText('Find us on:')).toBeVisible();
});

test('should display input form', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.getByText('Items per Page')).toBeVisible();
  await expect(page.locator('#pageSize')).toHaveValue('10');
  await expect(page.getByLabel('Scenario Name')).toBeVisible();
});

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


