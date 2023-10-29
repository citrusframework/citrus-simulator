import dayjs from 'dayjs/esm';
import { IScenarioExecution } from 'app/entities/scenario-execution/scenario-execution.model';

export interface IScenarioParameter {
  parameterId: number;
  name?: string | null;
  controlType?: number | null;
  value?: string | null;
  createdDate?: number | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  scenarioExecution?: Pick<IScenarioExecution, 'executionId' | 'scenarioName'> | null;
}

export type NewScenarioParameter = Omit<IScenarioParameter, 'parameterId'> & { parameterId: null };
