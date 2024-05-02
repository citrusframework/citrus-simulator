import dayjs from 'dayjs/esm';

import { IScenarioExecution, NewScenarioExecution } from './scenario-execution.model';

export const sampleWithRequiredData: IScenarioExecution = {
  executionId: 28068,
  startDate: dayjs('2023-10-18T19:26'),
  scenarioName: 'geez',
};

export const sampleWithPartialData: IScenarioExecution = {
  executionId: 6290,
  startDate: dayjs('2023-10-18T20:56'),
  scenarioName: 'baptise gracefully',
};

export const sampleWithFullData: IScenarioExecution = {
  executionId: 29940,
  startDate: dayjs('2023-10-19T02:53'),
  endDate: dayjs('2023-10-18T17:11'),
  scenarioName: 'midst',
};

export const sampleWithNewData: NewScenarioExecution = {
  startDate: dayjs('2023-10-18T16:49'),
  scenarioName: 'robotics',
  executionId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
