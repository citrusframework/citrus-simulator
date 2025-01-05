import dayjs from 'dayjs/esm';

import { ITestResult } from './test-result.model';

export const sampleWithRequiredData: ITestResult = {
  id: 25564,
  status: 10607,
  testName: 'why drag arrogantly',
  className: 'mash flight supposing',
  createdDate: dayjs('2023-09-26T04:15'),
  lastModifiedDate: dayjs('2023-09-26T09:35'),
};

export const sampleWithPartialData: ITestResult = {
  id: 23416,
  status: 12959,
  testName: 'including',
  className: 'untidy excepting',
  errorMessage: 'revitalise of',
  createdDate: dayjs('2023-09-25T22:16'),
  lastModifiedDate: dayjs('2023-09-26T01:57'),
};

export const sampleWithFullData: ITestResult = {
  id: 8640,
  status: 1743,
  testName: 'upon',
  className: 'each eek',
  errorMessage: 'anenst starboard',
  stackTrace: 'mmm mobilise because',
  failureType: 'gah measly amongst',
  createdDate: dayjs('2023-09-26T06:30'),
  lastModifiedDate: dayjs('2023-09-26T12:13'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
