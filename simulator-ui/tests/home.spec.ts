import { expect, test } from '@playwright/test';

test('should have title', async ({ page }) => {
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

test('should display disclaimer', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/disclaimer')).toBeVisible();
});

test('should display footer', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('footer/footer')).toBeVisible();
})

test('should display feedbackStarGithub', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/feedbackStarGithub')).toBeVisible();
})

test('should display refreshListButton', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('testResultsummary/refreshListButton')).toBeVisible();
})

test('should display resetButton', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('testResultSummary/resetButton')).toBeVisible();
})






