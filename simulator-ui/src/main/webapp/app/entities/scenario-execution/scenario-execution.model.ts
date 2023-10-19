import dayjs from 'dayjs/esm';

export interface IScenarioExecution {
  executionId: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioName?: string | null;
  status?: number | null;
  errorMessage?: string | null;
}

export type NewScenarioExecution = Omit<IScenarioExecution, 'executionId'> & { executionId: null };
