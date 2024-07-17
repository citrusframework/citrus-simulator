import { test, expect } from '@playwright/test';

test('has correct title', async ({ page }) => {
  await page.goto('http://localhost:9000/');

  // Expect a title "to contain" a substring.
  await expect(page).toHaveTitle(/Citrus Simulator/);
});

test('clicking Details button', async ({ page }) => {
  await page.goto('http://localhost:9000/');

  // Click the get started link.
  await page.locator('button:text("Details")');

  // Expects page to have a heading with the name of Installation.
  await expect(page.getByText('Scenario Executions')).toBeVisible();
});
