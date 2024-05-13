import dayjs from 'dayjs/esm';

import { IMessage } from 'app/entities/message/message.model';
import { IScenarioAction } from 'app/entities/scenario-action/scenario-action.model';
import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';
import { ITestResult } from 'app/entities/test-result/test-result.model';

export interface IScenarioExecution {
  executionId: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioName?: string | null;
  testResult?: ITestResult | null;
  scenarioActions?: IScenarioAction[] | null;
  scenarioMessages?: IMessage[] | null;
  scenarioParameters?: IScenarioParameter[] | null;
}
