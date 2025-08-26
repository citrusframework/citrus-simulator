import dayjs from 'dayjs/esm';

export interface IScenarioParameter {
  parameterId: number;
  name?: string | null;
  controlType?: number | null;
  value?: string | null;
  options?: IScenarioParameterOption[];
  createdDate?: number | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export interface IScenarioParameterOption {
  key: string;
  value: string;
}
