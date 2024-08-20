import {expect, test} from "@playwright/test";

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
  {"name": "HiStarter", "type": "STARTER"}
];

test.beforeEach(async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: scenarioJson});
  });
  await page.goto('http://localhost:9000/scenario');
})

test('text filter input should trigger the correct backend request and correct data should be displayed', async ({page}) => {
  const orderedJson = [
    {"name": "Fail", "type": "MESSAGE_TRIGGERED"},
    {"name": "Default", "type": "MESSAGE_TRIGGERED"},
    {"name": "ByeStarter", "type": "STARTER"},
    {"name": "HiStarter", "type": "STARTER"},
    {"name": "Parameter", "type": "MESSAGE_TRIGGERED"},
  ];

  //check if content is there --> and in the right ORDER not checket yet
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }

  await page.route('**/api/scenarios?page=0&size=10&nameContains=a&sort=id,asc', async route => {
    await route.fulfill({json: orderedJson});
  })

  await page.getByTestId('home/ScenarioNameFilterInput').fill('a');

  //check content --> ORDER not checked yet
  for (const element of orderedJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
});
