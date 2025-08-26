import dayjs from 'dayjs/esm';

import { IScenarioExecution } from './scenario-execution.model';

export const sampleWithRequiredData: IScenarioExecution = {
  executionId: 14992,
  startDate: dayjs('2023-10-19T10:21'),
  scenarioName: 'upon instantly',
  status: 21685,
};

export const sampleWithPartialData: IScenarioExecution = {
  executionId: 2462,
  startDate: dayjs('2023-10-18T22:00'),
  endDate: dayjs('2023-10-19T13:08'),
  scenarioName: 'supposing whose since',
  status: 3081,
  errorMessage: 'pick incidentally aside',
};

export const sampleWithFullData: IScenarioExecution = {
  executionId: 1969,
  startDate: dayjs('2023-10-19T03:05'),
  endDate: dayjs('2023-10-18T20:41'),
  scenarioName: 'satirise cuddly svelte',
  status: 19118,
  errorMessage: 'gosh',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
