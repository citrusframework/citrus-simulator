import { expect, Page } from '@playwright/test';
import { navbarElementLinkPair } from './helper-interfaces';

export const elementLinkPairNoDropdown: navbarElementLinkPair[] = [
  { testName: 'navigationScenariosLink', link: /.*scenario*/, apiLink: '**/api/scenarios*'},
  { testName: 'navigationScenarioExecutionsLink', link: /.*scenario-result*/, apiLink: '**/api/scenario-executions*' },
];
export const elementLinkPairEntityDroptdown: navbarElementLinkPair[] = [
  { testName: 'navigationEntitiesMessageLink', link: /.*\/message*/ , apiLink: '**/api/messages*'},
  { testName: 'navigationEntitiesMessageHeaderLink', link: /.*\/message-header*/, apiLink: '**/api/message-headers*'},
  { testName: 'navigationEntitiesScenarioExecutionLink', link: /.*\/scenario-execution*/, apiLink: '**/api/scenario-executions*'},
  { testName: 'navigationEntitiesScenarioActionLink', link: /.*\/scenario-action*/, apiLink: '**/api/scenario-actions*' },
  { testName: 'navigationEntitiesScenarioParameterLink', link: /.*\/scenario-parameter*/, apiLink: '**/api/scenario-parameters*' },
  { testName: 'navigationEntitiesTestResultLink', link: /.*\/test-result*/, apiLink: '**/api/test-results*' },
  { testName: 'navigationEntitiesParameterLink', link: /.*\/test-parameter*/, apiLink: '**/api/test-parameters*' },
];
// what to use as type instead of Promise<any>??
export const clickOnLinkAndCheckIfTabOpensWithCorrectURL = async (
  page: Page,
  linkTestSelector: string,
  expectedURL: RegExp,
): Promise<any> => {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent('popup'),

    page.getByTestId(linkTestSelector).click(),
  ]);
  await newTab.waitForLoadState();

  await expect(newTab).toHaveURL(expectedURL);
};

export const mockBackendResponse = async (page: Page, apiURL: string, responseJson: object, headers?:  {[key: string]: string; }): Promise<any> => {
  await page.route(apiURL, async route => {
    headers
      ? await route.fulfill({ json: responseJson , headers: headers })
      : await route.fulfill({ json: responseJson })
  });
};

export const mockErrorResponseOfAllApiUrls = async (page: Page): Promise<any> => {
  for (const element of elementLinkPairNoDropdown) {
    await page.route(element.apiLink, async route => {
      await route.fulfill({
        status: 500,
      });
    });
  }

  for (const element of elementLinkPairEntityDroptdown) {
    await page.route(element.apiLink, async route => {
      await route.fulfill({
        status: 500,
      });
    });
  }
};

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
};
