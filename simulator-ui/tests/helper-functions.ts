import {expect, Page} from "@playwright/test";

//what to use as type instead of Promise<any>??
export const clickOnLinkAndCheckIfTabOpensWithCorrectURL = async (page: Page, linkTestSelector: string, expectedURL: RegExp): Promise<any>=> {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent("popup"),

    page.getByTestId(linkTestSelector).click()
  ]);
  await newTab.waitForLoadState();

  // Interact with the new page normally.
  await expect(newTab).toHaveURL(expectedURL);
}

export const mockBackendResponse = async (page: Page, apiURL: string, responseJson: object): Promise<any> => {
  await page.route(apiURL, async route => {
    await route.fulfill({json: responseJson});
  })
}
