export interface EntityPageContentObject {
  testName: string;
  apiUrl: string;
  entityUrl: string;
  contentJson: object[];
  locators: string[];
  testIdsAndExpectedValues: TestIdValuePair[];
  testIdToBeVisible: string[];
}

interface TestIdValuePair {
  testId: string;
  expectedValue: string;
}

export interface navbarElementLinkPair {
  testName: string;
  link: RegExp;
  apiLink: string;
}
