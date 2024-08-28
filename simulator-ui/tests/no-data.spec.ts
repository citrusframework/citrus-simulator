import {expect, Page, test} from "@playwright/test";
import {goToAllPagesAndCheckURLPlusContent} from "./helpers/helper-functions";

test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
})

test('should show no-data-banner if there is an empty backend response on all pages', async ({page}) => {
  const checkIfNotFoundBannerVisible = async (page: Page) => {
    await expect(page.getByTestId('noDataFound')).toBeVisible();
  }
  await goToAllPagesAndCheckURLPlusContent(page, checkIfNotFoundBannerVisible);
})
