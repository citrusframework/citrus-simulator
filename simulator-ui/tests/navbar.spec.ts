import {expect, test} from "@playwright/test";

test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
})

test('should move to scenario page when navigating to scenario', async ({page}) => {
  await page.getByTestId('navigationScenariosLink').click();
  await expect(page).toHaveURL(/.*scenario*/);
})
test('should move to scenario-result when navigating to scenario executions', async ({page}) => {
  await page.getByTestId('navigationScenarioExecutionsLink').click();
  await expect(page).toHaveURL(/.*scenario-result*/);
})
test('should move to scenario-result and then move back home ', async ({page}) => {
  await page.getByTestId('navigationScenarioExecutionsLink').click();
  await expect(page).toHaveURL(/.*scenario-result*/);
  await page.getByTestId('navigationHomeLink').click();
  await expect(page).toHaveURL(/.*\//); //TODO: does this work properly?
})
