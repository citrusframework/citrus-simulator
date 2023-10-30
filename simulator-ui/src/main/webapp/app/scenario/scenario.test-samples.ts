import { IScenario } from './scenario.model';

export const sampleWithRequiredData: IScenario = {
  name: 'geez near',
  type: 'STARTER',
};

export const sampleWithPartialData: IScenario = {
  name: 'ah internationalise deliver',
  type: 'MESSAGE_TRIGGERED',
};

export const sampleWithFullData: IScenario = {
  name: 'oof continually after',
  type: 'STARTER',
};

Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
