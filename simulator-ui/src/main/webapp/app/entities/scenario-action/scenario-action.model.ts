import dayjs from 'dayjs/esm';
import { IScenarioExecution } from 'app/entities/scenario-execution/scenario-execution.model';

export interface IScenarioAction {
  actionId: number;
  name?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioExecution?: Pick<IScenarioExecution, 'executionId' | 'scenarioName'> | null;
}

export type NewScenarioAction = Omit<IScenarioAction, 'actionId'> & { actionId: null };
