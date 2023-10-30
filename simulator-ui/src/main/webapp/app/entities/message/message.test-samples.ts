import dayjs from 'dayjs/esm';

import { IMessage, NewMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  messageId: 4989,
  direction: 'INBOUND',
  citrusMessageId: 'fooey incidentally whether',
  createdDate: dayjs('2023-10-13T01:53'),
  lastModifiedDate: dayjs('2023-10-13T12:44'),
};

export const sampleWithPartialData: IMessage = {
  messageId: 11846,
  direction: 'OUTBOUND',
  citrusMessageId: 'ouch',
  createdDate: dayjs('2023-10-12T16:06'),
  lastModifiedDate: dayjs('2023-10-12T23:13'),
};

export const sampleWithFullData: IMessage = {
  messageId: 6858,
  direction: 'INBOUND',
  payload: 'yuck bite spectacular',
  citrusMessageId: 'bah who',
  createdDate: dayjs('2023-10-12T18:05'),
  lastModifiedDate: dayjs('2023-10-13T06:42'),
};

export const sampleWithNewData: NewMessage = {
  direction: 'UNKNOWN',
  citrusMessageId: 'cheap put',
  createdDate: dayjs('2023-10-12T23:44'),
  lastModifiedDate: dayjs('2023-10-13T00:54'),
  messageId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
