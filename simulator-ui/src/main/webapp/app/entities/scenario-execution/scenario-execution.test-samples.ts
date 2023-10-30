import dayjs from 'dayjs/esm';

import { IScenarioExecution, NewScenarioExecution } from './scenario-execution.model';

export const sampleWithRequiredData: IScenarioExecution = {
  executionId: 28068,
  startDate: dayjs('2023-10-18T19:26'),
  scenarioName: 'geez',
  status: 'RUNNING',
};

export const sampleWithPartialData: IScenarioExecution = {
  executionId: 6290,
  startDate: dayjs('2023-10-18T20:56'),
  scenarioName: 'baptise gracefully',
  status: 'SUCCESS',
  errorMessage: 'gee sniffle bunch',
};

export const sampleWithFullData: IScenarioExecution = {
  executionId: 29940,
  startDate: dayjs('2023-10-19T02:53'),
  endDate: dayjs('2023-10-18T17:11'),
  scenarioName: 'midst',
  status: 'FAILED',
  errorMessage: 'jiffy wherever',
};

export const sampleWithNewData: NewScenarioExecution = {
  startDate: dayjs('2023-10-18T16:49'),
  scenarioName: 'robotics',
  status: 'UNKNOWN',
  executionId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
