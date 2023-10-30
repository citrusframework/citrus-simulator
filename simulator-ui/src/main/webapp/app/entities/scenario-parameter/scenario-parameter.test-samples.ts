import dayjs from 'dayjs/esm';

import { IScenarioParameter, NewScenarioParameter } from './scenario-parameter.model';

export const sampleWithRequiredData: IScenarioParameter = {
  parameterId: 13246,
  name: 'TEXTBOX',
  controlType: 26023,
  value: 'underexpose since commerce',
  createdDate: 21593,
  lastModifiedDate: dayjs('2023-10-28T20:48'),
};

export const sampleWithPartialData: IScenarioParameter = {
  parameterId: 25616,
  name: 'TEXTAREA',
  controlType: 3663,
  value: 'outside hastily',
  createdDate: 26077,
  lastModifiedDate: dayjs('2023-10-28T23:22'),
};

export const sampleWithFullData: IScenarioParameter = {
  parameterId: 16729,
  name: 'DROPDOWN',
  controlType: 21298,
  value: 'coarse',
  createdDate: 9490,
  lastModifiedDate: dayjs('2023-10-29T08:59'),
};

export const sampleWithNewData: NewScenarioParameter = {
  name: 'UNKNOWN',
  controlType: 24095,
  value: 'whoa',
  createdDate: 11500,
  lastModifiedDate: dayjs('2023-10-28T17:10'),
  parameterId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
