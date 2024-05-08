import dayjs from 'dayjs/esm';

import { IScenarioAction } from './scenario-action.model';

export const sampleWithRequiredData: IScenarioAction = {
  actionId: 11639,
  name: 'genuflect woot absent',
  startDate: dayjs('2023-10-26T16:18'),
};

export const sampleWithPartialData: IScenarioAction = {
  actionId: 7876,
  name: 'offensively',
  startDate: dayjs('2023-10-26T06:41'),
};

export const sampleWithFullData: IScenarioAction = {
  actionId: 1734,
  name: 'except brr careless',
  startDate: dayjs('2023-10-26T10:55'),
  endDate: dayjs('2023-10-25T18:11'),
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
