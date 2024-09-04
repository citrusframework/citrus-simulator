import { expect, Page, test } from '@playwright/test';
import { goToAllPagesAndCheckURLPlusContent } from './helpers/helper-functions';

test.beforeEach(async ({ page }) => {
  await page.goto('http://localhost:9000/');
});

test('should show no-data-banner if there is an empty backend response on all pages', async ({ page }) => {
  await goToAllPagesAndCheckURLPlusContent(page, checkIfNotFoundBannerVisible);
});

const checkIfNotFoundBannerVisible = async (page: Page): Promise<void> => {
  await expect(page.getByTestId('noDataFound')).toBeVisible();
};
