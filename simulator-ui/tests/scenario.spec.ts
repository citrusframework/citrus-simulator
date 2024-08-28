import {expect, Page, test} from "@playwright/test";
import {mockBackendResponse} from "./helpers/helper-functions";

const scenarioJson = [
  {"name": "Default", "type": "MESSAGE_TRIGGERED"},
  {"name": "Fail", "type": "MESSAGE_TRIGGERED"},
  {"name": "GoodBye", "type": "MESSAGE_TRIGGERED"},
  {"name": "GoodNight", "type": "MESSAeGE_TRIGGERED"},
  {"name": "Hello", "type": "MESSAGE_TRIGGERED"},
  {"name": "Howdy", "type": "MESSAGE_TRIGGERED"},
  {"name": "Parm", "type": "MESSAGE_TRIGGERED"},
  {"name": "Throw", "type": "MESSAGE_TRIGGERED"},
  {"name": "ByeStarter", "type": "STARTER"},
  {"name": "HiStarter", "type": "STARTER"},
  {"name": "One", "type": "STARTER"},
  {"name": "Two", "type": "STARTER"}
];

const orderedJson = [
  {"name": "Fail", "type": "MESSAGE_TRIGGERED"},
  {"name": "Parm", "type": "MESSAGE_TRIGGERED"},
  {"name": "Default", "type": "MESSAGE_TRIGGERED"},
  {"name": "HiStarter", "type": "STARTER"},
  {"name": "ByeStarter", "type": "STARTER"},
];

const parameterJson = [
  {
    "parameterId" : null,
    "name": "payload",
    "controlType" : "TEXTAREA",
    "value" : "test value",
    "options" : [],
    "createdDate" : "2024-08-28T13:25:55.907136400Z",
    "lastModifiedDate": "2024-08-28T13:25:55.907136400Z"
  },
];

test.beforeEach(async ({page}) => {
  await page.route('**/api/scenarios?page=0&size=10&sort=name,asc', async route => {
    await route.fulfill({json: scenarioJson});
  });

  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('itemsPerPageSelect').selectOption('10');

})

test('should display all scenario information of a starter scenario', async ({page}) => {
  await mockBackendResponse(page, '**/api/scenarios**', {"name": "Test", "type": "STARTER"} );


  await page.goto('http://localhost:9000/scenario');

  await expect(page.getByTestId('scenarioEntitiesTable')).toBeVisible();
  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('STARTER');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should display all scenario information of a non-starter scenario', async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: [{"name": "Test", "type": "MESSAGE_TRIGGERED"}]});
  });

  await page.goto('http://localhost:9000/scenario');

  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('MESSAGE_TRIGGERED');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeHidden();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should have the first 10 of 12 elements displayed in the table and the pagination field should display it right', async ({page}) => {
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
});

// should one test EVERY option?
test('should have all 12 scenarios displayed in the table after selecting 20 as table size', async ({page}) => {
  const selectedPageSize = '20';
  await page.getByTestId('itemsPerPageSelect').selectOption(selectedPageSize);

  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue(selectedPageSize);
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  await expect(page.getByText('Showing 1-12 of 12 Items')).toBeVisible();
});

// how do you write this kind of test correctly? should the filter test be in the same as the reset button? or separate? Is the independence of the tests violated?
test('text filter input should apply and clear filter button should reset it', async ({page}) => {
  await applyFilterAAndCheckCorrectness(page);
  await page.getByTestId('clearFilterButton').click();

  await expect(page.getByTestId('scenarioFilterByNameInput')).toHaveValue('a');
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
});

// is the test independence again violated? should there be two separate tests for the pagination-select-option-button and the refresh button? --> no because the refresh button affects the pagination number inevitably.
test('should have updated displayed scenarios after refresh button was clicked and a new scenario received', async ({page}) => {
  const selectOptionsForNumberOfScenariosToDisplay: number[] = [10, 20, 50, 100];
  // should I really test the whole list?
  for (const option of selectOptionsForNumberOfScenariosToDisplay) {
    await page.getByTestId('itemsPerPageSelect').selectOption(option.toString());
    // should it be tested whether the start condition is met - like 'await checkIfAllJsonContentIsVisible(page, 20, scenarioJson);' - or not?

    scenarioJson.push({"name": "Three", "type": "STARTER"});
    await mockBackendResponse(page, '**/api/scenarios**', scenarioJson);

    await page.getByTestId('refreshListButton').click();
    await checkIfAllJsonContentIsVisible(page, option, scenarioJson);
    scenarioJson.pop();
  }
})

test('should move to the scenario-execution page with right filter entered if clicked on the execution button', async ({page}) => {
  const scenarioName = 'Test';
  const urlRegex = new RegExp(`.*scenario-result\\?.*filter%5BscenarioName\\.equals%5D=${scenarioName}`);

  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);
  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('scenarioExecutionButton').click();

  await expect(page).toHaveURL(urlRegex);
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toHaveValue(scenarioName);
})

test('should launch the scenario-execution and popup should be visible if clicked on the launch button', async ({page}) => {
  const scenarioId = [7];
  const scenarioName = 'Test'
  const urlRegex = new RegExp(`.*scenario-result\\?.*filter%5BexecutionId\\.in%5D=${scenarioId[0].toString()}`)
  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);

  await page.goto('http://localhost:9000/scenario');
  await mockBackendResponse(page, `**/api/scenarios/${scenarioName}/launch**`, scenarioId)
  await page.getByTestId('scenarioLaunchButton').click();
  await expect(page.getByText('Scenario successfully launched')).toBeVisible();
  await page.getByText('view Execution').click();

  await expect(page).toHaveURL(urlRegex)
  await expect(page.getByText('Following filters are set')).toBeVisible();
  await expect(page.getByText('executionId.in: ' + scenarioId[0])).toBeVisible();
})

test('should show error message if launch failed after click on the launch button', async ({page}) => {
  const scenarioName = 'Test'
  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);

  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('scenarioLaunchButton').click();

  await expect(page.getByText('Failed to launch Scenario!')).toBeVisible();
  await expect(page).toHaveURL(/.*localhost:9000\/scenario/);
})

test('should go to detail view of a scenario (not type starter) and then go back', async ({page}) => {
  const allVisibleDetailElements = ['scenarioDetailsHeading', 'scenarioDetailsName', 'scenarioDetailsType', 'scenarioDetailsEntitiesTable', 'entityDetailsBackButton']
  await page.getByText('Default').click();

  await expect(page).toHaveURL(/.*scenario\/Default\/MESSAGE_TRIGGERED\/view*/);
  for (const element of allVisibleDetailElements) {
    await expect(page.getByTestId(element)).toBeVisible();
  }
  await page.getByTestId('entityDetailsBackButton').click();
  await expect(page).toHaveURL(/.*scenario/);
})

test('should go to detail view of a scenario type STARTER check for content and go back', async ({page}) => {
  await mockScenarioParameters(page);
  const allVisibleDetailElements = ['scenarioDetailsHeading', 'scenarioDetailsName', 'scenarioDetailsType', 'scenarioDetailsEntitiesTable', 'scenarioDetailsEntitiesName', 'scenarioDetailsEntitiesControlType', 'scenarioDetailsEntitiesValue', 'entityDetailsBackButton']
  await page.getByText('One').click();

  // await mockScenarioParameters(page);
  await expect(page).toHaveURL(/.*scenario\/One\/STARTER\/view*/);
  for (const element of allVisibleDetailElements) {
    await expect(page.getByTestId(element)).toBeVisible();
  }
  await page.getByTestId('entityDetailsBackButton').click();
  await expect(page).toHaveURL(/.*scenario/);
})

const mockScenarioParameters = async (page: Page): Promise<any> => {
  await page.route('http://localhost:9000/api/scenarios/One/parameters', async route => {
    await route.fulfill({json: parameterJson});
  });
}

const checkIfAllJsonContentIsVisible = async (page: Page, selectedPageSize: number, responseJson: {
  name: string,
  type: string
}[]): Promise<any> => {
  const nbOfElemsInJson = responseJson.length;
  const nbOfDisplayedElems = selectedPageSize < nbOfElemsInJson ? selectedPageSize : nbOfElemsInJson;
  let counter = 0;

  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue(selectedPageSize.toString());
  for (const element of scenarioJson) {
    counter < nbOfDisplayedElems
      ? await expect(page.getByText(element.name)).toBeVisible()
      : await expect(page.getByText(element.name)).toBeHidden();
    counter++;
  }
  await expect(page.getByText(`Showing 1-${nbOfDisplayedElems} of ${nbOfElemsInJson} Items`)).toBeVisible();
}

const applyFilterAAndCheckCorrectness = async (page: Page): Promise<any> => {
  const pageSize: number = 20;
  await page.getByTestId('itemsPerPageSelect').selectOption(pageSize.toString());
  await checkIfAllJsonContentIsVisible(page, pageSize, scenarioJson);
  await mockBackendResponse(page, '**/api/scenarios?page=0&size=10&nameContains=a&sort=id,asc', orderedJson);

  await page.getByTestId('scenarioFilterByNameInput').fill('a');

  for (const element of scenarioJson) {
    element.name.includes('a') || element.name.includes('A') ? await expect(page.getByText(element.name)).toBeVisible() : await expect(page.getByText(element.name)).toBeHidden();
  }
  await expect(page.getByText('Showing 1-5 of 5 Items')).toBeVisible();
}
