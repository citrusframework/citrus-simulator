import { expect, Page, test } from '@playwright/test';

import { mockResponseForAllNavbarLinkedSites, navbarElementLinkPairs } from './helpers/helper-functions';
import { NavbarElementLinkPair } from './helpers/helper-interfaces';

const goToSiteAndVerifyErrorBannerIsVisible = (site: NavbarElementLinkPair): void => {
  test(`${site.testName}`, async ({ page }) => {
    if (site.apiLink && site.linkSuffix) {
      await mock500ErrorResponseForApiURL(page, site.apiLink);
      await page.goto(`http://localhost:9000${site.linkSuffix}`);
      await expect(page.getByTestId('error')).toBeVisible();
    }
  });
};

test.describe('should show error banner if there is a 500 error returned while loading any page', () => {
  test.beforeEach(async ({ page }) => {
    await mockResponseForAllNavbarLinkedSites(page, mock500ErrorResponseForApiURL);
  });

  navbarElementLinkPairs.forEach((element: NavbarElementLinkPair) => {
    if (element.childElements) {
      for (const child of element.childElements) {
        goToSiteAndVerifyErrorBannerIsVisible(child);
      }
    }
    goToSiteAndVerifyErrorBannerIsVisible(element);
  });
});

const mock500ErrorResponseForApiURL = async (page: Page, apiLink: string): Promise<void> => {
  await page.route(apiLink, async route => {
    await route.fulfill({
      status: 500,
      body: JSON.stringify({ message: 'hello' }),
    });
  });
};
