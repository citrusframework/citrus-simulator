import dayjs from 'dayjs/esm';
import { IMessage } from 'app/entities/message/message.model';

export interface IMessageHeader {
  headerId: number;
  name?: string | null;
  value?: string | null;
  message?: Pick<IMessage, 'messageId' | 'citrusMessageId'> | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewMessageHeader = Omit<IMessageHeader, 'headerId'> & { headerId: null };
