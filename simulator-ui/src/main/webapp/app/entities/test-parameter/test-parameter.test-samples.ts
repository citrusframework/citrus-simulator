import dayjs from 'dayjs/esm';

import { ITestParameter } from './test-parameter.model';

export const sampleWithRequiredData: ITestParameter = {
  id: 11612,
  key: 'blaspheme',
  value: 'cuddly fit gah',
  createdDate: dayjs('2023-09-26T15:33'),
  lastModifiedDate: dayjs('2023-09-26T05:33'),
};

export const sampleWithPartialData: ITestParameter = {
  id: 21792,
  key: 'however axe',
  value: 'reprove ugh exempt',
  createdDate: dayjs('2023-09-25T21:35'),
  lastModifiedDate: dayjs('2023-09-26T06:18'),
};

export const sampleWithFullData: ITestParameter = {
  id: 22589,
  key: 'notwithstanding disposer',
  value: 'timely defendant',
  createdDate: dayjs('2023-09-26T11:14'),
  lastModifiedDate: dayjs('2023-09-26T04:39'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
