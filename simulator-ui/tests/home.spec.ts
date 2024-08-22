import {expect, Page, test} from '@playwright/test';

import {clickOnLinkAndCheckIfTabOpensWithCorrectURL, mockBackendResponse} from "./helper-functions";

let nbOfSuccessfulTests = 90;
let nbOfFailedTests = 10;
let nbOfTotalTests = nbOfSuccessfulTests + nbOfFailedTests;

const scenarioSummariesLinkFilterTriples = [
  {'testName': 'totalSimulationsButton', 'link': /.*\/scenario-result*/, 'filterText': ''},
  {'testName': 'successfulSimulationsButton', 'link': /.*\/scenario-result*/, 'filterText': 'SUCCESSFUL'},
  {'testName': 'failedSimulationsButton', 'link': /.*\/scenario-result*/, 'filterText': 'FAILURE'},
]

test.beforeEach(async ({page}) => {
  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    "successful": nbOfSuccessfulTests,
    "failed": nbOfFailedTests,
    "total": nbOfTotalTests
  })
  await page.goto('http://localhost:9000/');
})

test('should have title, disclaimer, refresh button, reset button, feedback option, summary-tabs and footer', async ({page}) => {
  const visibleElements: string[] = [
    'disclaimer',
    'refreshListButton',
    'resetButton',
    'feedbackStarGithub',
    'footer',
    'totalSimulationsPercentage',
    'successfulSimulationsPercentage',
    'failedSimulationsPercentage'
  ]
  await expect(page).toHaveTitle(/Citrus Simulator/);
  for( const element of visibleElements){
    await expect(page.getByTestId(element)).toBeVisible();
  }
});

test('check if summary-tab displays right percentage with round numbers', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
})

test('total, successful, failed tabs should display percentage in simulations count rounded to two decimal numbers', async ({page}) => {
  const successfulTestsBig = 746039;
  const failedTestsBig = 490;
  const totalTestsBig = successfulTestsBig + failedTestsBig;

  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    "successful": successfulTestsBig,
    "failed": failedTestsBig,
    "total": totalTestsBig
  })
  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsBig, successfulTestsBig, failedTestsBig);
});

//should there be two separate tests for this?
test('should move to right page with feedback resp suggestion link', async ({page}) => {
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'feedbackLinkStarGithub', /.*\/github.com\/citrusframework\/citrus-simulator/)
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'feedbackAndSuggestionLink', /.*\/github.com\/citrusframework\/citrus-simulator\/issues\/new/)

})

test('should move to scenario-results page with right search field params after click on detail buttons', async ({page}) => {
  for (const element of scenarioSummariesLinkFilterTriples) {
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
    await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue(element.filterText);
  }
})

test('should have updated total, successful, failed tabs after refresh button clicked positive test', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);

  nbOfFailedTests -= 10;
  nbOfSuccessfulTests += 10;
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": nbOfSuccessfulTests, "failed": nbOfFailedTests, "total": nbOfTotalTests};
    await route.fulfill({json});
  });
  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
})

test('should have updated total, successful, failed tabs after refresh button clicked negative test', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  nbOfFailedTests -= 10; // so the Total will be wrong!
  const newCorrectTotal: number = nbOfTotalTests - 10;
  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    "successful": nbOfSuccessfulTests,
    "failed": nbOfFailedTests,
    "total": nbOfTotalTests
  })

  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, newCorrectTotal, nbOfSuccessfulTests, nbOfFailedTests);
})

test('should have same total, successful, failed tabs after cancel Deletion via close-Button and cancel-Button', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  const closeButtons = ['testResultDeleteDialogCloseButton', 'testResultDeleteDialogCancelButton'];

  for(const button of closeButtons){
    await page.getByTestId('resetButton').click();
    await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();
    await page.getByTestId(button).click();
    // HOW assert that API was NOT called? --> unit test?
    await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();

    await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  }
})

//should i make a method for the deleteReq/RespPromeises?
test('should have reset total, successful, failed tabs after confirmed Deletion with (200, OK) response', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);

  await page.getByTestId('resetButton').click();
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();

  nbOfSuccessfulTests = 0;
  nbOfFailedTests = 0;
  nbOfTotalTests = 0;

  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    "successful": nbOfSuccessfulTests,
    "failed": nbOfFailedTests,
    "total": nbOfTotalTests
  })

  const deleteRequestPromise = page.waitForRequest(request =>
    request.url() === '**/api/test-results/' && request.method() === 'DELETE',
  );
  const deleteResponsePromise = page.waitForResponse(response =>
    response.url() === '**/api/test-results/' && response.status() === 200
    && response.request().method() === 'DELETE'
  );

  await page.getByTestId('entityConfirmDeleteButton').click();

  await deleteRequestPromise;
  await deleteResponsePromise;

  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
})

const checkIfSummaryTabsAreDisplayingRightNumbersFunction = async (page: Page, totalTests: number, successfulTests: number, failedTests: number): Promise<any> => {
  await expect(page.getByTestId('totalSimulationsPercentage')).toHaveText(totalTests + ` (${(totalTests / totalTests * 100).toLocaleString(undefined, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0
  })} %)`);
  await expect(page.getByTestId('successfulSimulationsPercentage')).toHaveText(successfulTests + ` (${(successfulTests / totalTests * 100).toLocaleString(undefined, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0
  })} %)`);
  await expect(page.getByTestId('failedSimulationsPercentage')).toHaveText(failedTests + ` (${(failedTests / totalTests * 100).toLocaleString(undefined, {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0
  })} %)`);
}
