import dayjs from 'dayjs/esm';

import { IScenarioAction } from './scenario-action.model';

export const sampleWithRequiredData: IScenarioAction = {
  actionId: 17935,
  name: 'lest',
  startDate: dayjs('2023-10-26T00:16'),
};

export const sampleWithPartialData: IScenarioAction = {
  actionId: 25483,
  name: 'ugh ignorance',
  startDate: dayjs('2023-10-25T21:59'),
};

export const sampleWithFullData: IScenarioAction = {
  actionId: 5587,
  name: 'yowza',
  startDate: dayjs('2023-10-25T18:46'),
  endDate: dayjs('2023-10-26T14:10'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
