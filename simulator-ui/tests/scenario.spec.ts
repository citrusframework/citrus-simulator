import { expect, Page, test } from '@playwright/test';

import { mockBackendResponse } from './helpers/helper-functions';

const scenarioJson = [
  { name: 'Default', type: 'MESSAGE_TRIGGERED' },
  { name: 'Fail', type: 'MESSAGE_TRIGGERED' },
  { name: 'GoodBye', type: 'MESSAGE_TRIGGERED' },
  { name: 'GoodNight', type: 'MESSAeGE_TRIGGERED' },
  { name: 'Hello', type: 'MESSAGE_TRIGGERED' },
  { name: 'Howdy', type: 'MESSAGE_TRIGGERED' },
  { name: 'Parm', type: 'MESSAGE_TRIGGERED' },
  { name: 'Throw', type: 'MESSAGE_TRIGGERED' },
  { name: 'ByeStarter', type: 'STARTER' },
  { name: 'HiStarter', type: 'STARTER' },
];

const addititonalPageJson = [
  { name: 'One', type: 'STARTER' },
  { name: 'Two', type: 'STARTER' },
  { name: 'Three', type: 'STARTER' },
];

const orderedJson = [
  { name: 'Fail', type: 'MESSAGE_TRIGGERED' },
  { name: 'Parm', type: 'MESSAGE_TRIGGERED' },
  { name: 'Default', type: 'MESSAGE_TRIGGERED' },
  { name: 'HiStarter', type: 'STARTER' },
  { name: 'ByeStarter', type: 'STARTER' },
];

const parameterJson = [
  {
    parameterId: null,
    name: 'payload',
    controlType: 'TEXTAREA',
    value: 'test value',
    options: [],
    createdDate: '2024-08-28T13:25:55.907136400Z',
    lastModifiedDate: '2024-08-28T13:25:55.907136400Z',
  },
];

const availableScenarios: number = addititonalPageJson.length + scenarioJson.length;

test.beforeEach(async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenarios**page=0**', scenarioJson, { 'x-total-count': availableScenarios.toString() });
  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('itemsPerPageSelect').selectOption('10');
});

test('should display all scenario information of a starter scenario', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenarios**', [{ name: 'Test', type: 'STARTER' }], { 'x-total-count': '1' });

  await page.goto('http://localhost:9000/scenario');

  await expect(page.getByTestId('scenarioEntitiesTable')).toBeVisible();
  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('STARTER');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeVisible();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should display all scenario information of a non-starter scenario', async ({ page }) => {
  await mockBackendResponse(
    page,
    '**/api/scenarios**',
    [
      {
        name: 'Test',
        type: 'MESSAGE_TRIGGERED',
      },
    ],
    { 'x-total-count': '1' },
  );

  await page.goto('http://localhost:9000/scenario');

  await expect(page.getByTestId('scenarioEntitiesName')).toHaveText('Test');
  await expect(page.getByTestId('scenarioEntitiesType')).toHaveText('MESSAGE_TRIGGERED');
  await expect(page.getByTestId('scenarioLaunchButton')).toBeHidden();
  await expect(page.getByTestId('scenarioExecutionButton')).toBeVisible();
});

test('should have the first 10 of 13 elements displayed in the table and display right nb of visible elements', async ({ page }) => {
  await checkIfAllJsonContentIsVisible(page, 10, availableScenarios, scenarioJson);
});

test('should have the last 3 of 13 elements displayed in the table after clicking on 2. page', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenarios**page=1**', addititonalPageJson);
  await page.getByRole('link', { name: '2' }).click();
  await checkIfAllJsonContentIsVisible(page, 10, availableScenarios, addititonalPageJson);
});

test('see if frontend trusts backend to send only as much data as requested', async ({ page }) => {
  const selectedPageSize = 10;
  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue('10');

  scenarioJson.push({ name: 'One', type: 'STARTER' });
  await page.goto('http://localhost:9000/scenario');

  await page.getByTestId('itemsPerPageSelect').selectOption(selectedPageSize.toString());

  await checkIfAllJsonContentIsVisible(page, selectedPageSize, availableScenarios, scenarioJson);
  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue(selectedPageSize.toString());
  scenarioJson.pop();
});

test('text filter input should apply and clear filter button should reset it', async ({ page }) => {
  await applyFilterAAndCheckCorrectness(page);
  await page.getByTestId('clearFilterButton').click();

  await expect(page.getByTestId('scenarioFilterByNameInput')).toHaveValue('');
});

test('should have updated displayed scenarios after refresh button was clicked and a new scenario received', async ({ page }) => {
  const selectOptionsForNumberOfScenariosToDisplay: number[] = [10, 20, 50, 100];
  for (const option of selectOptionsForNumberOfScenariosToDisplay) {
    await page.getByTestId('itemsPerPageSelect').selectOption(option.toString());
    scenarioJson.pop();
    await mockBackendResponse(page, '**/api/scenarios**', scenarioJson, { 'x-total-count': scenarioJson.length.toString() });

    await page.getByTestId('refreshListButton').click();
    await checkIfAllJsonContentIsVisible(page, option, scenarioJson.length, scenarioJson);
    scenarioJson.push({ name: 'HiStarter', type: 'STARTER' });
  }
});

test('should move to the scenario-execution page with right filter entered if clicked on the execution button', async ({ page }) => {
  const scenarioName = 'Test';
  const urlRegex = new RegExp(`.*scenario-result\\?.*filter%5BscenarioName\\.equals%5D=${scenarioName}`);

  await mockBackendResponse(page, '**/api/scenarios**', [{ name: scenarioName, type: 'STARTER' }]);
  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('scenarioExecutionButton').click();

  await expect(page).toHaveURL(urlRegex);
  await expect(page.getByTestId('scenarioExecutionFilterInput')).toHaveValue(scenarioName);
});

test('should launch the scenario-execution and popup should be visible if clicked on the launch button', async ({ page }) => {
  const scenarioId = [7];
  const scenarioName = 'Test';
  const urlRegex = new RegExp(`.*scenario-result\\?.*filter%5BexecutionId\\.in%5D=${scenarioId[0].toString()}`);
  await mockBackendResponse(page, '**/api/scenarios**', [{ name: scenarioName, type: 'STARTER' }]);

  await page.goto('http://localhost:9000/scenario');
  await mockBackendResponse(page, `**/api/scenarios/${scenarioName}/launch**`, scenarioId);
  await page.getByTestId('scenarioLaunchButton').click();
  await expect(page.getByText('Scenario successfully launched')).toBeVisible();
  await page.getByText('view Execution').click();

  await expect(page).toHaveURL(urlRegex);
  await expect(page.getByText('Following filters are set')).toBeVisible();
  await expect(page.getByText('executionId.in: ' + scenarioId[0])).toBeVisible();
});

test('should show error message if launch failed after click on the launch button', async ({ page }) => {
  const scenarioName = 'Test';
  await mockBackendResponse(page, '**/api/scenarios**', [{ name: scenarioName, type: 'STARTER' }]);

  await page.goto('http://localhost:9000/scenario');
  await page.getByTestId('scenarioLaunchButton').click();

  await expect(page.getByText('Failed to launch Scenario!')).toBeVisible();
  await expect(page).toHaveURL(/.*localhost:9000\/scenario/);
});

test('should go to detail view of a scenario type MESSAGE_TRIGGERED check for content and and then go back', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenarios/Default/parameters', parameterJson);
  const allVisibleDetailElements = [
    'scenarioDetailsHeading',
    'scenarioDetailsName',
    'scenarioDetailsType',
    'scenarioDetailsEntitiesTable',
    'entityDetailsBackButton',
  ];

  await page.getByText('Default').click();

  await expect(page).toHaveURL(/.*scenario\/Default\/MESSAGE_TRIGGERED\/view*/);

  for (const element of allVisibleDetailElements) {
    await expect(page.getByTestId(element)).toBeVisible();
  }

  await page.getByTestId('entityDetailsBackButton').click();
  await expect(page).toHaveURL(/.*scenario/);
});

test('should go to detail view of a scenario type STARTER check for content and go back', async ({ page }) => {
  await mockBackendResponse(page, '**/api/scenarios/ByeStarter/parameters', parameterJson);
  const allVisibleDetailElements = [
    'scenarioDetailsHeading',
    'scenarioDetailsName',
    'scenarioDetailsType',
    'scenarioDetailsEntitiesTable',
    'scenarioDetailsEntitiesName',
    'scenarioDetailsEntitiesControlType',
    'scenarioDetailsEntitiesValue',
    'entityDetailsBackButton',
  ];

  await page.getByText('ByeStarter').click();

  await expect(page).toHaveURL(/.*scenario\/ByeStarter\/STARTER\/view*/);

  for (const element of allVisibleDetailElements) {
    await expect(page.getByTestId(element)).toBeVisible();
  }

  await page.getByTestId('entityDetailsBackButton').click();
  await expect(page).toHaveURL(/.*scenario/);
});

const checkIfAllJsonContentIsVisible = async (
  page: Page,
  selectedPageSize: number,
  totalElementsAvailable: number,
  responseJson: {
    name: string;
    type: string;
  }[],
): Promise<any> => {
  const nbOfDisplayedElems = selectedPageSize >= totalElementsAvailable ? totalElementsAvailable : selectedPageSize;

  await expect(page.getByTestId('itemsPerPageSelect')).toHaveValue(selectedPageSize.toString());
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  await expect(page.getByText(`Showing 1 - ${nbOfDisplayedElems} of ${totalElementsAvailable} items`)).toBeVisible();
};

const applyFilterAAndCheckCorrectness = async (page: Page): Promise<any> => {
  const pageSize: number = 20;
  await page.getByTestId('itemsPerPageSelect').selectOption(pageSize.toString());
  await checkIfAllJsonContentIsVisible(page, pageSize, availableScenarios, scenarioJson);
  await mockBackendResponse(page, '**/api/scenarios?**nameContains=a&sort=id,asc', orderedJson, { 'x-total-count': '5' });

  await page.getByTestId('scenarioFilterByNameInput').fill('a');

  for (const element of scenarioJson) {
    if (element.name.includes('a') || element.name.includes('A')) {
      await expect(page.getByText(element.name)).toBeVisible();
    } else {
      await expect(page.getByText(element.name)).toBeHidden();
    }
  }
  await expect(page.getByText('Showing 1 - 5 of 5 Items')).toBeVisible();
};
