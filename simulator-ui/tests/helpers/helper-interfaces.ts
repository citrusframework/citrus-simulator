export interface EntityPageContentObject {
  apiUrl: string,
  entityUrl: string,
  contentJson: {}[],
  locators: string[],
  testIdsAndExpectedValues: TestIdValuePair[],
  testIdToBeVisible: string[]
}

interface TestIdValuePair {
  id: string,
  value: string
}

export interface navbarElementLinkPair {
  testName: string, link: RegExp
}
