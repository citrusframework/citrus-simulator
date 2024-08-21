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
  {"name": "HiStarter", "type": "STARTER"},
  {"name": "One", "type": "STARTER"},
  {"name": "Two", "type": "STARTER"}
];

test.beforeEach(async ({page}) => {
  await page.route('**/api/scenarios**', async route => {
    await route.fulfill({json: scenarioJson});
  });
  await page.goto('http://localhost:9000/scenario');
})
test('should have the first 10 elements displayed in the table', async ({page}) => {
  for (const element of scenarioJson) {
    if(element.name != 'One' && element.name != 'Two') {
      await expect(page.getByText(element.name)).toBeVisible();
    }
  }
});

test('should have correct number of Elements displayed in the table after selecting 20 as table size', async ({page}) => {
  await expect(page.getByText('Showing 1-12 of 12 Items')).toBeVisible();
});
//order isn't correct...not bad because backend sends in order...? Or should backend request be made??? and tested???
test('text filter input should trigger the correct backend request and correct data should be displayed', async ({page}) => {
  const orderedJson = [
    {"name": "Fail", "type": "MESSAGE_TRIGGERED"},
    {"name": "Default", "type": "MESSAGE_TRIGGERED"},
    {"name": "ByeStarter", "type": "STARTER"},
    {"name": "HiStarter", "type": "STARTER"},
    {"name": "Parm", "type": "MESSAGE_TRIGGERED"},
  ];

  const noAJson = [
    {"name": "GoodBye", "type": "MESSAGE_TRIGGERED"},
    {"name": "GoodNight", "type": "MESSAGE_TRIGGERED"},
    {"name": "Hello", "type": "MESSAGE_TRIGGERED"},
    {"name": "Howdy", "type": "MESSAGE_TRIGGERED"},
    {"name": "Throw", "type": "MESSAGE_TRIGGERED"},
    {"name": "One", "type": "STARTER"},
    {"name": "Two", "type": "STARTER"}
  ]
  //check if content is there --> and in the right ORDER not checket yet
  for (const element of scenarioJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }

  await page.route('**/api/scenarios?page=0&size=10&nameContains=a&sort=id,asc', async route => {
    await route.fulfill({json: orderedJson});
  })

  await page.getByTestId('scenarioFilterByNameInput').fill('a');

  //check content --> ORDER not checked yet
  for (const element of orderedJson) {
    await expect(page.getByText(element.name)).toBeVisible();
  }
  //to be optimized in the same loop?
  for (const element of noAJson) {
    await expect(page.getByText(element.name)).toBeHidden();
  }
  await expect(page.getByText('Showing 1-5 of 5 Items')).toBeVisible();

});
