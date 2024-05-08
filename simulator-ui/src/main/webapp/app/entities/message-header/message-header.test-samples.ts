import dayjs from 'dayjs/esm';

import { IMessageHeader } from './message-header.model';

export const sampleWithRequiredData: IMessageHeader = {
  headerId: 29874,
  name: 'rapidly challenge',
  value: 'summon concerning',
  createdDate: dayjs('2023-10-17T20:12'),
  lastModifiedDate: dayjs('2023-10-18T16:07'),
};

export const sampleWithPartialData: IMessageHeader = {
  headerId: 7534,
  name: 'task',
  value: 'exclude',
  createdDate: dayjs('2023-10-17T22:05'),
  lastModifiedDate: dayjs('2023-10-18T03:27'),
};

export const sampleWithFullData: IMessageHeader = {
  headerId: 32408,
  name: 'but phooey since',
  value: 'concerning spotlight',
  createdDate: dayjs('2023-10-17T18:11'),
  lastModifiedDate: dayjs('2023-10-18T15:28'),
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
