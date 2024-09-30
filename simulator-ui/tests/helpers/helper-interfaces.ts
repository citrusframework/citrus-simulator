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

export interface NavbarElementLinkPair {
  testName: string;
  expectedLinkRegex?: RegExp;
  linkSuffix?: string;
  apiLink?: string;
  childElements?: NavbarElementLinkPair[];
}
