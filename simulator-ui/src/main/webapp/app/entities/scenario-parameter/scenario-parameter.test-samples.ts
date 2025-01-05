import dayjs from 'dayjs/esm';

import { IScenarioParameter } from './scenario-parameter.model';

export const sampleWithRequiredData: IScenarioParameter = {
  parameterId: 9777,
  name: 'freely and',
  controlType: 29065,
  value: 'short-term orchid',
  createdDate: 22088,
  lastModifiedDate: dayjs('2023-10-29T06:27'),
};

export const sampleWithPartialData: IScenarioParameter = {
  parameterId: 25519,
  name: 'robust',
  controlType: 24972,
  value: 'quick-witted',
  createdDate: 26724,
  lastModifiedDate: dayjs('2023-10-29T07:44'),
};

export const sampleWithFullData: IScenarioParameter = {
  parameterId: 12100,
  name: 'sniveling blowgun',
  controlType: 23759,
  value: 'hm economise',
  createdDate: 23081,
  lastModifiedDate: dayjs('2023-10-29T12:21'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
