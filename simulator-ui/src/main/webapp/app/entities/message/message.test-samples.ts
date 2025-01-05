import dayjs from 'dayjs/esm';

import { IMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  messageId: 10168,
  direction: 5485,
  citrusMessageId: 'voluntarily vacantly for',
  createdDate: dayjs('2023-10-13T12:58'),
  lastModifiedDate: dayjs('2023-10-13T10:43'),
};

export const sampleWithPartialData: IMessage = {
  messageId: 30142,
  direction: 6322,
  payload: 'yahoo prioritize',
  citrusMessageId: 'splurge',
  createdDate: dayjs('2023-10-13T05:46'),
  lastModifiedDate: dayjs('2023-10-13T01:07'),
};

export const sampleWithFullData: IMessage = {
  messageId: 2775,
  direction: 27778,
  payload: 'midst comparison where',
  citrusMessageId: 'what',
  createdDate: dayjs('2023-10-12T19:51'),
  lastModifiedDate: dayjs('2023-10-12T19:37'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
