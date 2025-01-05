import dayjs from 'dayjs/esm';
import { ITestResult } from '../test-result/test-result.model';
import { IMessage } from '../message/message.model';
import { IScenarioAction } from '../scenario-action/scenario-action.model';
import { IScenarioParameter } from '../scenario-parameter/scenario-parameter.model';

export interface IScenarioExecution {
  executionId: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioName?: string | null;
  testResult?: ITestResult | null;
  scenarioParameters?: IScenarioParameter[] | null;
  scenarioActions?: IScenarioAction[] | null;
  scenarioMessages?: IMessage[] | null;
}
