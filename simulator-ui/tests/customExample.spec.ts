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
  await page.goto('http://localhost:9000/');

  await expect(page.getByText('Total:')).toBeVisible();
  await expect(page.getByText('Successful:')).toBeVisible();
  await expect(page.getByText('Failed:')).toBeVisible();
});

test('total, successful, failed tabs should display simulations count', async ({page}) => {
  await page.route('*/**/api/test-results/count-by-status', async route => {
    const json = {"successful": 746039, "failed": 490, "total": 746529};
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/');

  await expect(page.getByText('746529')).toBeVisible();
  await expect(page.getByText('746039')).toBeVisible();
  await expect(page.getByText('490')).toBeVisible();
});

test('total, successful, failed tabs should display percentage in simulations count', async ({page}) => {
  await page.route('*/**/api/test-results/count-by-status', async route => {
    const json = {"successful": 746039, "failed": 490, "total": 746529};
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/');

  await expect(page.getByText('100 %')).toBeVisible();
  await expect(page.getByText('99.93 %')).toBeVisible();
  await expect(page.getByText('0.07 %')).toBeVisible();
});

test('should click Details button', async ({page}) => {
  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.getByText('Items per Page')).toBeVisible();
});

test('should display list categories', async ({page}) => {
  await page.route('*/**/api/scenario-executions*', async route => {
    const json = [
      {
        "executionId": 752603,
        "startDate": "2024-06-27T10:59:03.872021Z",
        "endDate": "2024-06-27T10:59:04.168103Z",
        "scenarioName": "Default",
        "testResult": {
          "id": 752026,
          "status": "FAILURE",
          "errorMessage": "Unable to validate because message store is not of type 'CorrelatedMessageProvider'! Check your configuration and register a suitable message store.",
          "createdDate": "2024-06-27T08:59:04.159168Z",
        },
      }
    ];
    await route.fulfill({json});
  });

  await page.goto('http://localhost:9000/scenario-result/');

  await expect(page.locator('table th :text("ID")')).toHaveCount(1);
  await expect(page.locator('table th :text("Scenario Name")')).toHaveCount(1);
  await expect(page.locator('table th :text("Start Date")')).toHaveCount(1);
  await expect(page.locator('table th :text("End Date")')).toHaveCount(1);
  await expect(page.locator('table th :text("Status")')).toHaveCount(1);
  await expect(page.locator('table th :text("Error Message")')).toHaveCount(1);
});
