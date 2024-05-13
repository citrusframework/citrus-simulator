import dayjs from 'dayjs/esm';

import { IMessage } from 'app/entities/message/message.model';
import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';
import { ITestResult } from 'app/entities/test-result/test-result.model';

export interface IScenarioExecution {
  executionId: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  scenarioName?: string | null;
  testResult?: ITestResult | null;
  scenarioMessages?: IMessage[] | null;
  scenarioParameters?: IScenarioParameter[] | null;
}
