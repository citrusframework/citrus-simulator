import {expect, Page, test} from "@playwright/test";
import {mockBackendResponse} from "./helper-functions";

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

test.beforeEach(async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: scenarioJson});
  });
  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('itemsPerPageSelect').selectOption('10');

})

test('should display all scenario information of a starter scenario', async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: [{"name": "Test", "type": "STARTER"}]});
  });
  await page.goto('http://localhost:9000/scenario');
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

//should one test EVERY option?
test('should have all 12 scenarios displayed in the table after selecting 20 as table size', async ({page}) => {
  await page.getByTestId('itemsPerPageSelect').selectOption('20');
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  await expect(page.getByText('Showing 1-12 of 12 Items')).toBeVisible();
});

//how do you write this kind of test correctly? should the filter test be in the same as the reset button? or separate? Test independency violated?
test('text filter input should apply and clear filter button should reset it', async ({page}) => {
  await applyFilterAAndCheckCorrectness(page);
  await page.getByTestId('clearFilterButton').click();

  await expect(page.getByTestId('scenarioFilterByNameInput')).toHaveValue('a');
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
});

//maybe put these two tests together in one function
test('should have updated displayed scenarios (11 from max 11) and pagination number after refresh button clicked', async ({page}) => {
  //should I really test the whole list?
  await page.getByTestId('itemsPerPageSelect').selectOption('20');
  //await checkIfAllJsonContentIsVisible(page, 20, scenarioJson); should this be tested everytime?

  scenarioJson.push({"name": "Three", "type": "STARTER"});
  await mockBackendResponse(page, '**/api/scenarios**', scenarioJson);

  await page.getByTestId('refreshListButton').click();
  await checkIfAllJsonContentIsVisible(page, 20, scenarioJson);
  scenarioJson.pop();
})

test('should have updated the pagination-number  but not the displayed scenarios (10 from 11) after clicking refresh button', async ({page}) => {
  //should I really test the whole list?
  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue('10');
  //await checkIfAllJsonContentIsVisible(page, 10, scenarioJson); should this be tested everytime?

  scenarioJson.push({"name": "Three", "type": "STARTER"});
  await mockBackendResponse(page, '**/api/scenarios**', scenarioJson);

  await page.getByTestId('refreshListButton').click();
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
})


test('should move to the scenario-execution page with right filter entered if clicked on the execution button', async ({page}) => {
  const scenarioName = 'Test';
  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);
  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('scenarioExecutionButton').click();
  //how to regex this?
  await expect(page).toHaveURL(/.*scenario-result.*filter%5BscenarioName.equals%5D=/ + scenarioName);
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toHaveValue(scenarioName);
})

test('should launch the scenario-execution and popup should be visible if clicked on the launch button', async ({page}) => {
  const scenarioId = [7];
  const scenarioName = 'Test'
  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);
  await page.goto('http://localhost:9000/scenario');

  await mockBackendResponse(page, `**/api/scenarios/${scenarioName}/launch**`, scenarioId)
  await page.getByTestId('scenarioLaunchButton').click();
  await expect(page.getByText('Scenario successfully launched')).toBeVisible();
  await page.getByText('view Execution').click();
  //how to regex this?
  await expect(page).toHaveURL(/.*scenario-result.*filter%5BexecutionId.in%5D=*/ + scenarioId[0].toString())
  await expect(page.getByText('Following filters are set')).toBeVisible();
  await expect(page.getByText('executionId.in: ' + scenarioId)).toBeVisible();
})

test('should show error message if launch failed after click on the launch button', async ({page}) => {
  const scenarioId = [7];
  const scenarioName = 'Test'
  await mockBackendResponse(page, '**/api/scenarios**', [{"name": scenarioName, "type": "STARTER"}]);
  await page.goto('http://localhost:9000/scenario');

  await page.getByTestId('scenarioLaunchButton').click();

  await expect(page.getByText('Failed to launch Scenario!')).toBeVisible();
  await expect(page).toHaveURL(/.*localhost:9000\/scenario/);
})


const checkIfAllJsonContentIsVisible = async (page: Page, selectedPageSize: number, responseJson: {
  name: string,
  type: string
}[]): Promise<any> => {
  const nbOfElemsInJson = responseJson.length;
  const nbOfDisplayedElems = selectedPageSize < nbOfElemsInJson ? selectedPageSize : nbOfElemsInJson;
  let counter = 0;


  console.log(nbOfDisplayedElems);
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
  //check if content is there --> and in the right ORDER not checket yet
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  await mockBackendResponse(page, '**/api/scenarios?page=0&size=10&nameContains=a&sort=id,asc', orderedJson);

  await page.getByTestId('scenarioFilterByNameInput').fill('a');

  //check content --> ORDER not checked
  for (const element of scenarioJson) {
    element.name.includes('a') || element.name.includes('A') ? await expect(page.getByText(element.name)).toBeVisible() : await expect(page.getByText(element.name)).toBeHidden();
  }
  await expect(page.getByText('Showing 1-5 of 5 Items')).toBeVisible();
}
