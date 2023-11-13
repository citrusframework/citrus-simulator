import dayjs from 'dayjs/esm';

export interface IScenarioExecution {
  executionId: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioName?: string | null;
  status?: 'UNKNOWN' | 'RUNNING' | 'SUCCESS' | 'FAILED' | null;
  errorMessage?: string | null;
}

export type NewScenarioExecution = Omit<IScenarioExecution, 'executionId'> & { executionId: null };

export interface IScenarioExecutionStatus {
  id: number;
  name: 'UNKNOWN' | 'RUNNING' | 'SUCCESS' | 'FAILED';
}

export const STATUS_UNKNOWN: IScenarioExecutionStatus = { id: 0, name: 'UNKNOWN' };
export const STATUS_RUNNING: IScenarioExecutionStatus = { id: 1, name: 'RUNNING' };
export const STATUS_SUCCESS: IScenarioExecutionStatus = { id: 2, name: 'SUCCESS' };
export const STATUS_FAILED: IScenarioExecutionStatus = { id: 3, name: 'FAILED' };

export const scenarioExecutionStatusFromName = (name: string): IScenarioExecutionStatus => {
  for (const status of [STATUS_UNKNOWN, STATUS_RUNNING, STATUS_SUCCESS, STATUS_FAILED]) {
    if (status.name === name) {
      return status;
    }
  }

  return STATUS_UNKNOWN;
};

export const scenarioExecutionStatusFromId = (id: number): IScenarioExecutionStatus => {
  for (const status of [STATUS_UNKNOWN, STATUS_RUNNING, STATUS_SUCCESS, STATUS_FAILED]) {
    if (status.id === id) {
      return status;
    }
  }

  return STATUS_UNKNOWN;
};
