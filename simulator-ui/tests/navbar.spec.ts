import { expect, test } from '@playwright/test';

import { clickOnLinkAndCheckIfTabOpensWithCorrectURL, goToAllNavigationTabsAndOptionallyValidateContent } from './helpers/helper-functions';

test.beforeEach(async ({ page }) => {
  await page.goto('http://localhost:9000/');
});

test('should move to all pages as intended', async ({ page }) => {
  await goToAllNavigationTabsAndOptionallyValidateContent(page);
});

test('should move to scenario-result and then move back home ', async ({ page }) => {
  await page.getByTestId('navigationScenarioExecutionsLink').click();
  await expect(page).toHaveURL(/.*scenario-result*/);
  await page.getByTestId('navigationHomeLink').click();
  await expect(page).toHaveURL(/.*\//);
});

test('should move to the documentation in a new Tab', async ({ page }) => {
  await page.getByTestId('entity').click();
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'documentation', /.*\/\/citrusframework.org\/citrus-simulator/);
});

test('should move to the swaggerUI documentation', async ({ page }) => {
  await page.getByTestId('entity').click();
  await page.getByTestId('swaggerUI').click();
  await expect(page).toHaveURL(/.*swagger-ui\/index.html/);
});
