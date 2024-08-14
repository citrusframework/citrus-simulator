import {expect, Page, test} from '@playwright/test';

test('should have title', async ({ page }) => {
  await page.goto('http://localhost:9000/');

  await expect(page).toHaveTitle(/Citrus Simulator/);
});

test('should have refresh list, and reset button', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/refreshListButton')).toBeVisible();
  await expect(page.getByTestId('home/resetButton')).toBeVisible();
});

test('should have total, successful, failed tabs', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfullTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  const successfullTests = 90;
  const failedTests = 10;
  const totalTests = successfullTests + failedTests;

  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/totalTestsSummary')).toBeVisible();
  await expect(page.getByTestId('home/successfulTestsSummary')).toBeVisible();
  await expect(page.getByTestId('home/failedTestsSummary')).toBeVisible();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfullTests, failedTests);

});

test('total, successful, failed tabs should display percentage in simulations count', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  const successfulTests = 746039;
  const failedTests = 490;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
});

test('should display disclaimer', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/disclaimer')).toBeVisible();
});

test('should display footer', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('footer/footer')).toBeVisible();
})

test('should display feedbackStarGithub', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/feedbackStarGithub')).toBeVisible();
})

test('should display refreshListButton', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/refreshListButton')).toBeVisible();
})

test('should display resetButton', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await expect(page.getByTestId('home/resetButton')).toBeVisible();
})

test('should move to right page with Github link', async ({page}) => {
  await page.goto('http://localhost:9000/');

  const [newTab] = await Promise.all([
  // Start waiting for new page before clicking. Note no await.
    page.waitForEvent("popup"),
    page.getByTestId('home/feedbackLinkStarGithub').click()
  ]);

  await newTab.waitForLoadState();

// Interact with the new page normally.
  await expect(page).toHaveURL('https://github.com/citrusframework/citrus-simulator/');
})
test('should move to right page with citrus link', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await page.getByTestId('home/feedbackAndSuggestionLink').click();
  await expect(page).toHaveURL('https://github.com/citrusframework/citrus-simulator/issues/new');
})

test('should move to scenario-results page with right search field params after click on total-details', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await page.getByTestId('home/totalDetailsBtn').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc');
  await expect(page.getByTestId('scenarioExecutionFilter/status')).toHaveValue('');
})

test('should move to scenario-results page with right search field params after click on successful-details', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await page.getByTestId('home/successfulDetailsBtn').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc&filter%5Bstatus.equals%5D=1');
  await expect(page.getByTestId('scenarioExecutionFilter/status')).toHaveValue('SUCCESS');
})

test('should move to scenario-results page with right search field params after click on failed-details', async ({page}) => {
  await page.goto('http://localhost:9000/');

  await page.getByTestId('home/failedDetailsBtn').click();

  await expect(page).toHaveURL('http://localhost:9000/scenario-result?page=1&size=10&sort=executionId,asc&filter%5Bstatus.equals%5D=2');
  await expect(page.getByTestId('scenarioExecutionFilter/status')).toHaveValue('FAILURE');
})

test('should have updated total, successful, failed tabs after refresh button clicked positive test', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  let successfulTests = 90;
  let failedTests = 10;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);

  failedTests-=10;
  successfulTests+=10;
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  await page.getByTestId('home/refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
})

test('should have updated total, successful, failed tabs after refresh button clicked negative test', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  let successfulTests = 90;
  let failedTests = 10;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);

  failedTests-=10; // so the Total will be wrong!
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  await page.getByTestId('home/refreshListButton').click();

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
})

test('should have same total, successful, failed tabs after cancel Deletion via cross-Button', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  let successfulTests = 90;
  let failedTests = 10;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);

  await page.getByTestId('home/resetButton').click();
  await expect(page.getByTestId('home/deletePopup')).toBeVisible();
  await page.getByTestId('home/cancelDeleteButtonCross').click();
  //HOW assert that API was NOT called?
  await expect(page.getByTestId('home/deletePopup'), undefined).toBeVisible(); //invisible?

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
})

test('should have same total, successful, failed tabs after cancel Deletion via cancel-Button', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  let successfulTests = 90;
  let failedTests = 10;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);

  await page.getByTestId('home/resetButton').click();
  await expect(page.getByTestId('home/deletePopup')).toBeVisible();
  await page.getByTestId('home/cancelDeleteButton').click();
  //HOW assert that API was NOT called?
  await expect(page.getByTestId('home/deletePopup'), undefined).toBeVisible(); //invisible?

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
})

test('should have reset total, successful, failed tabs after confirmed Deletion with (200, OK) response', async ({page}) => {
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  });
  let successfulTests = 90;
  let failedTests = 10;
  const totalTests = successfulTests + failedTests;

  await page.goto('http://localhost:9000/');
  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);

  await page.getByTestId('home/resetButton').click();
  await expect(page.getByTestId('home/deletePopup')).toBeVisible();

  successfulTests = 0;
  failedTests = 0;
  await page.route('**/api/test-results/count-by-status', async route => {
    const json = {"successful": successfulTests, "failed": failedTests, "total": totalTests};
    await route.fulfill({json});
  })
  const deleteRequestPromise = page.waitForRequest(request =>
    request.url() === '**/api/test-results/' && request.method() === 'DELETE',
  );
  const deleteResponsePromise = page.waitForResponse(response =>
    response.url() === '**/api/test-results/' && response.status() === 200
    && response.request().method() === 'DELETE'
  );
  await page.getByTestId('home/confirmDeleteButton').click();

  await deleteRequestPromise;
  await deleteResponsePromise;

  await expect(page.getByTestId('home/deletePopup'), undefined).toBeVisible(); //invisible?

  await checkIfSummaryTabsAreDisplayingRightNumbersFunction(page, totalTests, successfulTests, failedTests);
})



let checkIfSummaryTabsAreDisplayingRightNumbersFunction = async (page: Page, totalTests: number, successfulTests: number, failedTests: number) => {
  await expect(page.getByTestId('home/totalTestsNumber')).toHaveText(totalTests + ` (${(totalTests/totalTests*100).toLocaleString(undefined, {maximumFractionDigits: 2, minimumFractionDigits: 0})} %)`);
  await expect(page.getByTestId('home/successfulTestsNumber')).toHaveText(successfulTests + ` (${(successfulTests/totalTests*100).toLocaleString(undefined, {maximumFractionDigits: 2, minimumFractionDigits: 0})} %)`);
  await expect(page.getByTestId('home/failedTestsNumber')).toHaveText(failedTests+ ` (${(failedTests/totalTests*100).toLocaleString(undefined, {maximumFractionDigits: 2, minimumFractionDigits: 0})} %)`);
}
