import dayjs from 'dayjs/esm';
import { ITestResult } from 'app/entities/test-result/test-result.model';

export interface ITestParameter {
  key?: string | null;
  value?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  testResult: Pick<ITestResult, 'id'>;
}
