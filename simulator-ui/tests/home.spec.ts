import { expect, Page, Route, test } from '@playwright/test';

import { clickOnLinkAndCheckIfTabOpensWithCorrectURL, mockBackendResponse } from './helpers/helper-functions';

let nbOfSuccessfulTests: number;
let nbOfFailedTests: number;
let nbOfTotalTests: number;

const scenarioSummariesLinkFilterTriples = [
  { testName: 'totalSimulationsButton', link: /.*\/scenario-result*/, filterText: '' },
  { testName: 'successfulSimulationsButton', link: /.*\/scenario-result*/, filterText: 'SUCCESS' },
  { testName: 'failedSimulationsButton', link: /.*\/scenario-result*/, filterText: 'FAILURE' },
];

test.beforeEach(async ({ page }) => {
  nbOfSuccessfulTests = 90;
  nbOfFailedTests = 10;
  nbOfTotalTests = nbOfSuccessfulTests + nbOfFailedTests;

  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    successful: nbOfSuccessfulTests,
    failed: nbOfFailedTests,
    total: nbOfTotalTests,
  });
  await mockBackendResponse(page, '**/api/manage/info', {
    config: { 'reset-results-enabled': 'true' },
  });
  await page.goto('http://localhost:9000/');
});

test('should have title, disclaimer, refresh button, reset button, feedback option, summary-tabs and footer', async ({ page }) => {
  const visibleElements: string[] = [
    'disclaimer',
    'refreshListButton',
    'resetButton',
    'feedbackStarGithub',
    'footer',
    'totalSimulationsPercentage',
    'successfulSimulationsPercentage',
    'failedSimulationsPercentage',
  ];

  await expect(page).toHaveTitle(/Citrus Simulator/);
  for (const element of visibleElements) {
    await expect(page.getByTestId(element)).toBeVisible();
  }
});

test('check if summary-tab displays right percentage with round numbers', async ({ page }) => {
  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
});

test('total, successful, failed tabs should display percentage in simulations count rounded to two decimal numbers', async ({ page }) => {
  const successfulTestsBig = 746039;
  const failedTestsBig = 490;
  const totalTestsBig = successfulTestsBig + failedTestsBig;

  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    successful: successfulTestsBig,
    failed: failedTestsBig,
    total: totalTestsBig,
  });
  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbers(page, totalTestsBig, successfulTestsBig, failedTestsBig);
});

test('should move to right page with feedback resp suggestion link', async ({ page }) => {
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'feedbackLinkStarGithub', /.*\/github\.com\/citrusframework\/citrus-simulator/);
  await clickOnLinkAndCheckIfTabOpensWithCorrectURL(page, 'feedbackAndSuggestionLink', /.*\/github\.com.*issue/);
});

test('should move to scenario-results page with right search field params after click on detail buttons', async ({ page }) => {
  for (const element of scenarioSummariesLinkFilterTriples) {
    await page.goto('http://localhost:9000/');
    await page.getByTestId(element.testName).click();
    await expect(page).toHaveURL(element.link);
    await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue(element.filterText);
  }
});

test('should have updated total, successful, failed tabs after refresh button clicked positive test', async ({ page }) => {
  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);

  nbOfFailedTests -= 10;
  nbOfSuccessfulTests += 10; // the total stays the same
  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    successful: nbOfSuccessfulTests,
    failed: nbOfFailedTests,
    total: nbOfTotalTests,
  });
  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
});

test('(test if frontend trusts backend blindly) should have updated total, successful, failed tabs after refresh button clicked negative test with false total', async ({
  page,
}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  nbOfFailedTests -= 10; // so the total will be wrong!
  const newCorrectTotal: number = nbOfTotalTests - 10;
  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    successful: nbOfSuccessfulTests,
    failed: nbOfFailedTests,
    total: nbOfTotalTests,
  });

  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  expect(nbOfTotalTests === newCorrectTotal).toBeFalsy();
});

test('should have same total, successful, failed tabs after cancel deletion via close-Button and cancel-Button', async ({ page }) => {
  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
  const closeButtons = ['testResultDeleteDialogCloseButton', 'testResultDeleteDialogCancelButton'];
  let deleteRequestWasMade = false;

  page.on('request', request => {
    if (request.method() === 'DELETE') {
      deleteRequestWasMade = true;
    }
  });

  for (const button of closeButtons) {
    await page.getByTestId('resetButton').click();
    await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();
    await page.getByTestId(button).click();
    await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();

    await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);
    expect(deleteRequestWasMade).toBe(false);
  }
});

test('should have reset total, successful, failed tabs after confirmed deletion with (200, OK) response', async ({ page }) => {
  await checkIfSummaryTabsAreDisplayingRightNumbers(page, nbOfTotalTests, nbOfSuccessfulTests, nbOfFailedTests);

  await page.getByTestId('resetButton').click();
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();

  await page.route('**/api/test-results', async (route: Route) => {
    if (route.request().method() === 'DELETE') {
      await route.fulfill({ status: 200 });
    }
  });

  await mockBackendResponse(page, '**/api/test-results/count-by-status', {
    successful: 0,
    failed: 0,
    total: 0,
  });

  await page.getByTestId('entityConfirmDeleteButton').click();

  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();
  await expect(page.getByTestId('noSimulationsRanBanner')).toBeVisible();
});

const checkIfSummaryTabsAreDisplayingRightNumbers = async (
  page: Page,
  totalTests: number,
  successfulTests: number,
  failedTests: number,
): Promise<any> => {
  const summarySelectorToAbsoluteValueMapping: {
    testSelector: string;
    value: number;
  }[] = [
    {
      testSelector: 'totalSimulationsPercentage',
      value: totalTests,
    },
    {
      testSelector: 'successfulSimulationsPercentage',
      value: successfulTests,
    },
    {
      testSelector: 'failedSimulationsPercentage',
      value: failedTests,
    },
  ];

  for (const percentageDisplay of summarySelectorToAbsoluteValueMapping) {
    await expect(page.getByTestId(percentageDisplay.testSelector)).toHaveText(
      percentageDisplay.value +
        ` (${((percentageDisplay.value / totalTests) * 100).toLocaleString(undefined, {
          maximumFractionDigits: 2,
          minimumFractionDigits: 0,
        })} %)`,
    );
  }
};
