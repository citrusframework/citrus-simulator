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

test('should display all information of a starter scenario', async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: [{"name": "Test", "type": "STARTER"}]});
  });
  await page.goto('http://localhost:9000/scenario');
  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('STARTER');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should display all information of a non-starter scenario', async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: [{"name": "Test", "type": "MESSAGE_TRIGGERED"}]});
  });
  await page.goto('http://localhost:9000/scenario');
  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('MESSAGE_TRIGGERED');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeHidden();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should have the first 10 elements displayed in the table', async ({page}) => {
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
});

//should one test EVERY option?
test('should have correct number of Elements displayed in the table after selecting 20 as table size', async ({page}) => {
  await page.getByTestId('itemsPerPageSelect').selectOption('20');
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  await expect(page.getByText('Showing 1-12 of 12 Items')).toBeVisible();
});

//how do you write this kind of test correctly? should the filter test be in the same as the reset button? or separate?
test('text filter input should apply and clear filter button should reset it', async ({page}) => {
  await applyFilterAAndCheckCorrectness(page);
  await page.getByTestId('clearFilterButton').click();

  await expect(page.getByTestId('scenarioFilterByNameInput')).toHaveValue('a');
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
});

test('should have updated scenarios after refresh button clicked', async ({page}) => {
  //should I really test the whole list?
  await page.getByTestId('itemsPerPageSelect').selectOption('20');
  await checkIfAllJsonContentIsVisible(page, 20, scenarioJson);

  scenarioJson.push({"name": "Three", "type": "STARTER"});
  await mockBackendResponse(page, '**/api/scenarios**', scenarioJson);

  await page.getByTestId('refreshListButton').click();
  await checkIfAllJsonContentIsVisible(page, 20, scenarioJson);
  scenarioJson.pop();
})

test('should have updated scenarios after refresh button clicked but not the display only the pagination-number', async ({page}) => {
  //should I really test the whole list?
  await page.getByTestId('itemsPerPageSelect').selectOption('10');
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);

  scenarioJson.push({"name": "Three", "type": "STARTER"});
  await mockBackendResponse(page, '**/api/scenarios**', scenarioJson);

  await page.getByTestId('refreshListButton').click();
  await checkIfAllJsonContentIsVisible(page, 10, scenarioJson);
})

//TODO: fix this test to work
test('should move to the scenario-execution page with right filter entered if clicked on the execution button', async ({page}) => {
  //andy fragen, wie er den button selected da es ja viele davon gibt.
  await page.getByTestId('scenarioExecutionButton').click();
  const scenarioName = 'Hello';
  //how to regex this?
  await expect(page).toHaveURL(`.*scenario-result\\?&filter\\[scenarioName.equals]=${scenarioName}`);
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toHaveValue(scenarioName);
})

test('should launch the scenario-execution and popup should be visible if clicked on the launch button', async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: [{"name": "Test", "type": "STARTER"}]});
  });
  await page.goto('http://localhost:9000/scenario');

  await page.getByTestId('scenarioLaunchButton').click();
  const scenarioName = 'Test';
  //how to regex this?
  await expect(page).toHaveURL(`.*scenario-result\\?&filter\\[scenarioName.equals]=${scenarioName}`)
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toHaveValue(scenarioName);
  //mockBackendResponse(page, '**/api/scenarios/GoodByeStarter/launch'); //muss das überhaupt sein, wäre ja unit test?
  //alert gets triggered....thats why i make "getbyText"
  await expect(page.getByText('Scenario successfully launched')).toBeVisible();
  await page.getByText('view Execution').click();
  await expect(page).toHaveURL(`.*scenario-result?&filter\\[executionId.in]=\\d+\\`);
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
