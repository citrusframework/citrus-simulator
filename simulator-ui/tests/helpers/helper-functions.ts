import {expect, Page} from "@playwright/test";
import {navbarElementLinkPair} from "./helper-interfaces";

export const elementLinkPairNoDropdown: navbarElementLinkPair[] = [
  {testName: 'navigationScenariosLink', link: /.*scenario*/},
  {testName: 'navigationScenarioExecutionsLink', link: /.*scenario-result*/}
]
export const elementLinkPairEntityDroptdown: navbarElementLinkPair[] = [
  {testName: 'navigationEntitiesMessageLink', link: /.*\/message*/},
  {testName: 'navigationEntitiesMessageHeaderLink', link: /.*\/message-header*/},
  {testName: 'navigationEntitiesScenarioExecutionLink', link: /.*\/scenario-execution*/},
  {testName: 'navigationEntitiesScenarioActionLink', link: /.*\/scenario-action*/},
  {testName: 'navigationEntitiesScenarioParameterLink', link: /.*\/scenario-parameter*/},
  {testName: 'navigationEntitiesTestResultLink', link: /.*\/test-result*/},
  {testName: 'navigationEntitiesParameterLink', link: /.*\/test-parameter*/},
]
//what to use as type instead of Promise<any>??
export const clickOnLinkAndCheckIfTabOpensWithCorrectURL = async (page: Page, linkTestSelector: string, expectedURL: RegExp): Promise<any> => {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent("popup"),

    page.getByTestId(linkTestSelector).click()
  ]);
  await newTab.waitForLoadState();

  await expect(newTab).toHaveURL(expectedURL);
}

export const mockBackendResponse = async (page: Page, apiURL: string, responseJson: object): Promise<any> => {
  await page.route(apiURL, async route => {
    await route.fulfill({json: responseJson});
  })
}

export const goToAllPagesAndCheckURLPlusContent = async (page: Page, checkPageContentFunction?: (page: Page) => void): Promise<any> => {
  for (const element of elementLinkPairNoDropdown) {
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
    if (checkPageContentFunction) {
      checkPageContentFunction(page);
    }
  }
  for (const element of elementLinkPairEntityDroptdown) {
    await page.getByTestId('navigationEntitiesLink').click();
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
    if (checkPageContentFunction) {
      checkPageContentFunction(page);
    }
  }
}