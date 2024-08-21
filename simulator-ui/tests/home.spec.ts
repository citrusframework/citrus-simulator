import {expect, Page, test} from '@playwright/test';

let successfulTestsMock = 90;
let failedTestsMock = 10;
let totalTestsMock = successfulTestsMock + failedTestsMock;

test.beforeEach(async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTestsMock, "failed": failedTestsMock, "total": totalTestsMock};
    await route.fulfill({json});
  });
  await page.goto('http://localhost:9000/');
})

test('should have title, disclaimer, refresh button, reset button, feedback option, summary-tabs and footer', async ({page}) => {
  await expect(page).toHaveTitle(/Citrus Simulator/);
  await expect(page.getByTestId('disclaimer')).toBeVisible();
  await expect(page.getByTestId('refreshListButton')).toBeVisible();
  await expect(page.getByTestId('resetButton')).toBeVisible();
  await expect(page.getByTestId('feedbackStarGithub')).toBeVisible();
  await expect(page.getByTestId('footer')).toBeVisible();
  await expect(page.getByTestId('totalSimulationsPercentage')).toBeVisible();
  await expect(page.getByTestId('successfulSimulationsPercentage')).toBeVisible();
  await expect(page.getByTestId('failedSimulationsPercentage')).toBeVisible();
});

test('check if summary-tab displays right percentage with round numbers', async ({page}) =>{
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);
})

test('total, successful, failed tabs should display percentage in simulations count rounded to two decimal numbers', async ({page}) => {
  const successfulTestsBig = 746039;
  const failedTestsBig = 490;
  const totalTestsBig = successfulTestsBig + failedTestsBig;

  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTestsBig, "failed": failedTestsBig, "total": totalTestsBig};
    await route.fulfill({json});
  });
  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsBig, successfulTestsBig, failedTestsBig);
});

test('should move to right page with Github link', async ({page}) => {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent("popup"),
    page.getByTestId('feedbackLinkStarGithub').click()
  ]);

  await newTab.waitForLoadState();

// Interact with the new page normally.
  await expect(newTab).toHaveURL('https://github.com/citrusframework/citrus-simulator');
})
test('should move to right page with citrus link', async ({page}) => {
  const [newTab] = await Promise.all([
    // Start waiting for new page before clicking. Note no await.
    page.waitForEvent("popup"),
    page.getByTestId('feedbackAndSuggestionLink').click()
  ]);

  await newTab.waitForLoadState();

  await expect(newTab).toHaveURL(RegExp('https://github.com*'));
})

test('should move to scenario-results page with right search field params after click on total-details', async ({page}) => {
  await page.getByTestId('totalSimulationsButton').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc');
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue('');
})

test('should move to scenario-results page with right search field params after click on successful-details', async ({page}) => {
  await page.getByTestId('successfulSimulationsButton').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc&filter%5Bstatus.equals%5D=1');
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue('SUCCESS');
})

test('should move to scenario-results page with right search field params after click on failed-details', async ({page}) => {
  await page.getByTestId('failedSimulationsButton').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc&filter%5Bstatus.equals%5D=2');
  await expect(page.getByTestId('scenarioExecutionStatusInSelect')).toHaveValue('FAILURE');
})

test('should have updated total, successful, failed tabs after refresh button clicked positive test', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);

  failedTestsMock -= 10;
  successfulTestsMock += 10;
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTestsMock, "failed": failedTestsMock, "total": totalTestsMock};
    await route.fulfill({json});
  });
  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);
})

test('should have updated total, successful, failed tabs after refresh button clicked negative test', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);

  failedTestsMock -= 10; // so the Total will be wrong!
  const newCorrectTotal: number = totalTestsMock - 10;
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTestsMock, "failed": failedTestsMock, "total": totalTestsMock};
    await route.fulfill({json});
  });
  await page.getByTestId('refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, newCorrectTotal, successfulTestsMock, failedTestsMock);
})

test('should have same total, successful, failed tabs after cancel Deletion via cross-Button', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);

  await page.getByTestId('resetButton').click();
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();
  await page.getByTestId('testResultDeleteDialogCloseButton').click();
  // HOW assert that API was NOT called?
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);
})

test('should have same total, successful, failed tabs after cancel Deletion via cancel-Button', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);

  await page.getByTestId('resetButton').click();
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();
  await page.getByTestId('testResultDeleteDialogCancelButton').click();
  // HOW assert that API was NOT called?
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeHidden();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);
})

test('should have reset total, successful, failed tabs after confirmed Deletion with (200, OK) response', async ({page}) => {
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);

  await page.getByTestId('resetButton').click();
  await expect(page.getByTestId('testResultDeleteDialogHeading')).toBeVisible();

  successfulTestsMock = 0;
  failedTestsMock = 0;
  totalTestsMock = 0;

  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTestsMock, "failed": failedTestsMock, "total": totalTestsMock};
    await route.fulfill({json});
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

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTestsMock, successfulTestsMock, failedTestsMock);
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
