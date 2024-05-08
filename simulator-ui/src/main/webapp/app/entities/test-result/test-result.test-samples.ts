import dayjs from 'dayjs/esm';

import { ITestResult } from './test-result.model';

export const sampleWithRequiredData: ITestResult = {
  id: 22758,
  status: 'SUCCESS',
  testName: 'breadcrumb',
  className: 'newspaper',
  createdDate: dayjs('2023-09-26T09:11'),
  lastModifiedDate: dayjs('2023-09-26T14:25'),
};

export const sampleWithPartialData: ITestResult = {
  id: 1008,
  status: 'FAILURE',
  testName: 'zowie',
  className: 'regarding openly',
  errorMessage: 'toward',
  stackTrace: 'whose',
  createdDate: dayjs('2023-09-26T13:39'),
  lastModifiedDate: dayjs('2023-09-26T15:03'),
};

export const sampleWithFullData: ITestResult = {
  id: 11970,
  status: 'SKIP',
  testName: 'peruse probable display',
  className: 'dining',
  errorMessage: 'reproachfully better what',
  stackTrace: 'flugelhorn over',
  failureType: 'aha',
  createdDate: dayjs('2023-09-26T07:09'),
  lastModifiedDate: dayjs('2023-09-26T09:18'),
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
