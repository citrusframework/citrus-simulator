import dayjs from 'dayjs/esm';

import { IScenarioAction, NewScenarioAction } from './scenario-action.model';

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

export const sampleWithNewData: NewScenarioAction = {
  name: 'dearly',
  startDate: dayjs('2023-10-26T11:00'),
  actionId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
