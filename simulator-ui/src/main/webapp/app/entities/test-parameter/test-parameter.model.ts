import dayjs from 'dayjs/esm';

export interface ITestParameter {
  key: string;
  testResultId: number;
  value?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}
