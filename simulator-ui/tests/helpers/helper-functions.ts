import { expect, Page } from '@playwright/test';
import { NavbarElementLinkPair } from './helper-interfaces';

// a list of every navbar link-pair, which appears under the "Entity" dropdown
export const entityChildLinks: NavbarElementLinkPair[] = [
  { testName: 'navigationEntitiesMessageLink', expectedLinkRegex: /.*\/message*/, linkSuffix: '/message', apiLink: '**/api/messages*' },
  {
    testName: 'navigationEntitiesMessageHeaderLink',
    expectedLinkRegex: /.*\/message-header*/,
    linkSuffix: '/message-header',
    apiLink: '**/api/message-headers*',
  },
  {
    testName: 'navigationEntitiesScenarioExecutionLink',
    expectedLinkRegex: /.*\/scenario-execution*/,
    linkSuffix: '/scenario-execution',
    apiLink: '**/api/scenario-executions*',
  },
  {
    testName: 'navigationEntitiesScenarioActionLink',
    expectedLinkRegex: /.*\/scenario-action*/,
    linkSuffix: '/scenario-action',
    apiLink: '**/api/scenario-actions*',
  },
  {
    testName: 'navigationEntitiesScenarioParameterLink',
    expectedLinkRegex: /.*\/scenario-parameter*/,
    linkSuffix: '/scenario-parameter',
    apiLink: '**/api/scenario-parameters*',
  },
  {
    testName: 'navigationEntitiesTestResultLink',
    expectedLinkRegex: /.*\/test-result*/,
    linkSuffix: '/test-result',
    apiLink: '**/api/test-results*',
  },
  {
    testName: 'navigationEntitiesParameterLink',
    expectedLinkRegex: /.*\/test-parameter*/,
    linkSuffix: '/test-parameter',
    apiLink: '**/api/test-parameters*',
  },
];

// a list of every navbar element, which leads directly to another page
export const navbarElementLinkPairs: NavbarElementLinkPair[] = [
  { testName: 'navigationScenariosLink', expectedLinkRegex: /.*\/scenario*/, linkSuffix: '/scenario', apiLink: '**/api/scenarios*' },
  {
    testName: 'navigationScenarioExecutionsLink',
    expectedLinkRegex: /.*\/scenario-result*/,
    linkSuffix: '/scenario-result',
    apiLink: '**/api/scenario-executions*',
  },
  { testName: 'navigationEntitiesLink', childElements: entityChildLinks },
];

export const clickOnLinkAndCheckIfTabOpensWithCorrectURL = async (
  page: Page,
  linkTestSelector: string,
  expectedURL: RegExp,
): Promise<void> => {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent('popup'),
    page.getByTestId(linkTestSelector).click(),
  ]);

  await newTab.waitForLoadState();

  await expect(newTab).toHaveURL(expectedURL);
};

export const mockBackendResponse = async (
  page: Page,
  apiURL: string,
  responseJson: object,
  headers?: { [key: string]: string },
): Promise<void> => {
  await page.route(apiURL, async route => {
    if (headers) {
      await route.fulfill({ json: responseJson, headers });
    } else {
      await route.fulfill({ json: responseJson });
    }
  });
};

export const mockResponseForAllNavbarLinkedSites = async (
  page: Page,
  responseMockFunction: (page: Page, apiLink: string) => Promise<void>,
): Promise<void> => {
  for (const element of navbarElementLinkPairs) {
    if (element.childElements) {
      for (const child of element.childElements) {
        if (child.apiLink) {
          await responseMockFunction(page, child.apiLink);
        }
      }
    }
    if (element.apiLink) {
      await responseMockFunction(page, element.apiLink);
    }
  }
};

export const goToAllNavigationTabsAndOptionallyValidateContent = async (
  page: Page,
  validatePageContent?: (page: Page) => Promise<void>,
): Promise<void> => {
  for (const element of navbarElementLinkPairs) {
    if (element.childElements) {
      for (const child of element.childElements) {
        await page.getByTestId(element.testName).click();
        await clickOnNavbarElementAndOptionallyValidateContent(page, child, validatePageContent);
      }
    }
    await clickOnNavbarElementAndOptionallyValidateContent(page, element, validatePageContent);
  }
};

const clickOnNavbarElementAndOptionallyValidateContent = async (
  page: Page,
  navbarElement: NavbarElementLinkPair,
  validatePageContent?: (page: Page) => Promise<void>,
): Promise<void> => {
  await page.getByTestId(navbarElement.testName).click();
  if (navbarElement.expectedLinkRegex) {
    await expect(page).toHaveURL(navbarElement.expectedLinkRegex);
  }
  if (validatePageContent) {
    await validatePageContent(page);
  }
};
