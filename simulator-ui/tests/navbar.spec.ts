import {expect, Page, test} from "@playwright/test";
import {clickOnLinkAndCheckIfTabOpensWithCorrectURL} from "./helper-functions";

const elementLinkPairNoDropdown = [
  {'testName': 'navigationScenariosLink', 'link': /.*scenario*/},
  {'testName': 'navigationScenarioExecutionsLink', 'link': /.*scenario-result*/}
]
const elementLinkPairEntityDroptdown = [
  {'testName': 'navigationEntitiesMessageLink', 'link': /.*\/message*/},
  {'testName': 'navigationEntitiesMessageHeaderLink', 'link': /.*\/message-header*/},
  {'testName': 'navigationEntitiesScenarioExecutionLink', 'link': /.*\/scenario-execution*/},
  {'testName': 'navigationEntitiesScenarioActionLink', 'link': /.*\/scenario-action*/},
  {'testName': 'navigationEntitiesScenarioParameterLink', 'link': /.*\/scenario-parameter*/},
  {'testName': 'navigationEntitiesTestResultLink', 'link': /.*\/test-result*/},
  {'testName': 'navigationEntitiesParameterLink', 'link': /.*\/test-parameter*/},
]
test.beforeEach(async ({page}) => {
  await page.goto('http://localhost:9000/');
})

test('should move to scenario page when navigating to scenario resp. scenario-result', async ({page}) => {
  for (const element of elementLinkPairNoDropdown) {
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
  }
})

test('should move to scenario-result and then move back home ', async ({page}) => {
  await page.getByTestId('navigationScenarioExecutionsLink').click();
  await expect(page).toHaveURL(/.*scenario-result*/);
  await page.getByTestId('navigationHomeLink').click();
  await expect(page).toHaveURL(/.*\//);
})
test('should move to all entities pages as intended', async ({page}) => {
  for (const element of elementLinkPairEntityDroptdown) {
    await page.getByTestId('navigationEntitiesLink').click();
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
  }
})

test('should move to the documentation in a new Tab', async ({page}) => {
  await page.getByTestId('entity').click();
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'documentation', /.*\/\/citrusframework.org\/citrus-simulator/);
})

test('should move to the swaggerUI documentation', async ({page}) => {
  await page.getByTestId('entity').click();
  await page.getByTestId('swaggerUI').click();
  await expect(page).toHaveURL(/.*swagger-ui\/index.html/);
})
