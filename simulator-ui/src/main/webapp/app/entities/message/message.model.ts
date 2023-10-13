import dayjs from 'dayjs/esm';

export interface IMessage {
  messageId: number;
  direction?: number | null;
  payload?: string | null;
  citrusMessageId?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewMessage = Omit<IMessage, 'messageId'> & { messageId: null };
