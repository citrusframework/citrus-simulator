import {expect, Page, test} from "@playwright/test";
import {goToAllPagesAndCheckURLPlusContent,} from "./helpers";

test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
  await page.route('**/api/scenarios**', async (route) => {
    await route.fulfill({
      status: 500,
    });
  });
})

test('should show error-banner if there is an error code in the backend response while loading any page', async ({page}) => {
   const thingsToCheckOnAllPages = async (page: Page) => {
     await expect(page.getByTestId('alert')).toBeVisible();
     await expect(page.getByTestId('error')).toBeVisible();
   }
  await goToAllPagesAndCheckURLPlusContent(page, thingsToCheckOnAllPages);
})
