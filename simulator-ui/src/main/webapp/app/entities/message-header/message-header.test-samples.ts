import { IMessageHeader, NewMessageHeader } from './message-header.model';

export const sampleWithRequiredData: IMessageHeader = {
  headerId: 463,
  name: 'knavishly',
  value: 'however evenly',
};

export const sampleWithPartialData: IMessageHeader = {
  headerId: 6641,
  name: 'searchingly blah',
  value: 'yesterday',
};

export const sampleWithFullData: IMessageHeader = {
  headerId: 26555,
  name: 'sheepishly mismanage ornery',
  value: 'upbeat accomplish',
};

export const sampleWithNewData: NewMessageHeader = {
  name: 'playground superior nimble',
  value: 'fairly',
  headerId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
