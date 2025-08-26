import dayjs from 'dayjs/esm';
import { IMessageHeader } from '../message-header/message-header.model';

export interface IMessage {
  messageId: number;
  direction?: 'UNKNOWN' | 'INBOUND' | 'OUTBOUND' | null;
  payload?: string | null;
  citrusMessageId?: string | null;
  headers?: IMessageHeader[] | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}
