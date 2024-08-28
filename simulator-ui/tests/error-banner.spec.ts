import {expect, Page, test} from "@playwright/test";
import {goToAllPagesAndCheckURLPlusContent, mockErrorResponseOfAllApiUrls,} from "./helpers/helper-functions";

test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
  await page.route('**!/api/scenarios**', async (route) => {
    await route.fulfill({
      status: 500,
    });
  });
})

test('should show error banner if there is an error code in the backend response while loading any page', async ({page}) => {
   const thingsToCheckOnAllPages = async (pageToPass: Page): Promise<void> => {
     await expect(pageToPass.getByTestId('error')).toBeVisible();
   }
  await mockErrorResponseOfAllApiUrls(page);
  await goToAllPagesAndCheckURLPlusContent(page, thingsToCheckOnAllPages);
})
