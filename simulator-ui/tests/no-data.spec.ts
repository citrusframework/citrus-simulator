import { expect, Page, test } from '@playwright/test';

import {
  goToAllNavigationTabsAndOptionallyValidateContent,
  mockBackendResponse,
  mockResponseForAllNavbarLinkedSites,
} from './helpers/helper-functions';

test.beforeEach(async ({ page }) => {
  await mockResponseForAllNavbarLinkedSites(page, mockEmptyResponseForApiURL);
  await page.goto('http://localhost:9000/');
});

test('should show no-data-banner if there is an empty backend response on all pages', async ({ page }) => {
  await goToAllNavigationTabsAndOptionallyValidateContent(page, verifyNoDataFoundBannerIsVisible);
});

const verifyNoDataFoundBannerIsVisible = async (page: Page): Promise<void> => {
  await expect(page.getByTestId('noDataFound')).toBeVisible();
};

const mockEmptyResponseForApiURL = async (page: Page, apiLink: string): Promise<void> => {
  await mockBackendResponse(page, apiLink, [], { 'x-total-count': '0' });
};
