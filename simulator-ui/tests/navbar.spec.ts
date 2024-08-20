import {expect, test} from "@playwright/test";

test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
})

test('should move to scenario page when navigating to scenario', async ({page}) => {
  await page.getByTestId('navbar/scenario').click();
  await expect(page).toHaveURL(/.*scenario*/);
})
test('should move to scenario-result when navigating to scenario exectuions', async ({page}) => {
  await page.getByTestId('navbar/scenario-executions').click();
  await expect(page).toHaveURL(/.*scenario-result*/);
})
