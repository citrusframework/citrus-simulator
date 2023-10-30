import dayjs from 'dayjs/esm';

import { IScenarioExecution } from 'app/entities/scenario-execution/scenario-execution.model';

export interface IMessage {
  messageId: number;
  direction?: 'UNKNOWN' | 'INBOUND' | 'OUTBOUND' | null;
  payload?: string | null;
  citrusMessageId?: string | null;
  scenarioExecutionId?: number | null;
  scenarioName?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewMessage = Omit<IMessage, 'messageId'> & { messageId: null };
