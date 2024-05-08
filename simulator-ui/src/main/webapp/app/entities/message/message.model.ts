import dayjs from 'dayjs/esm';

import { IMessageHeader } from 'app/entities/message-header/message-header.model';

export interface IMessage {
  messageId: number;
  direction?: 'UNKNOWN' | 'INBOUND' | 'OUTBOUND' | null;
  payload?: string | null;
  citrusMessageId?: string | null;
  scenarioExecutionId?: number | null;
  scenarioName?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  headers?: IMessageHeader[] | null;
}
