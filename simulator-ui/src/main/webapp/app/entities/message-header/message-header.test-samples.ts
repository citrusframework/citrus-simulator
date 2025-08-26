import dayjs from 'dayjs/esm';

import { IMessageHeader } from './message-header.model';

export const sampleWithRequiredData: IMessageHeader = {
  headerId: 18898,
  name: 'demob inasmuch section',
  value: 'fooey',
  createdDate: dayjs('2023-10-18T16:52'),
  lastModifiedDate: dayjs('2023-10-17T23:18'),
};

export const sampleWithPartialData: IMessageHeader = {
  headerId: 1469,
  name: 'modulo',
  value: 'ugh',
  createdDate: dayjs('2023-10-17T17:24'),
  lastModifiedDate: dayjs('2023-10-17T20:04'),
};

export const sampleWithFullData: IMessageHeader = {
  headerId: 28119,
  name: 'lest recklessly if',
  value: 'blowgun',
  createdDate: dayjs('2023-10-18T17:02'),
  lastModifiedDate: dayjs('2023-10-18T09:29'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
