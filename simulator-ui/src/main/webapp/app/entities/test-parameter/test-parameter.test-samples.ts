import dayjs from 'dayjs/esm';

import { ITestParameter } from './test-parameter.model';

export const sampleWithRequiredData: ITestParameter = {
  key: 'indeed on chance',
  value: 'too junior',
  createdDate: dayjs('2023-09-26T08:48'),
  lastModifiedDate: dayjs('2023-09-26T12:17'),
  testResult: {
    id: 79374,
  },
};

export const sampleWithPartialData: ITestParameter = {
  key: 'gel supposing for',
  value: 'yuck whereas as',
  createdDate: dayjs('2023-09-26T16:06'),
  lastModifiedDate: dayjs('2023-09-26T06:13'),
  testResult: {
    id: 84283,
  },
};

export const sampleWithFullData: ITestParameter = {
  key: 'inter hm ew',
  value: 'gently',
  createdDate: dayjs('2023-09-26T13:02'),
  lastModifiedDate: dayjs('2023-09-26T17:35'),
  testResult: {
    id: 15826,
  },
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
