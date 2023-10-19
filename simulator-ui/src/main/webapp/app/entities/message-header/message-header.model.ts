import { IMessage } from 'app/entities/message/message.model';

export interface IMessageHeader {
  headerId: number;
  name?: string | null;
  value?: string | null;
  message?: Pick<IMessage, 'messageId' | 'citrusMessageId'> | null;
}

export type NewMessageHeader = Omit<IMessageHeader, 'headerId'> & { headerId: null };
