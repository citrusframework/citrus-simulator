import dayjs from 'dayjs/esm';

export interface ITestResult {
  id: number;
  status?: number | null;
  testName?: string | null;
  className?: string | null;
  errorMessage?: string | null;
  failureStack?: string | null;
  failureType?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewTestResult = Omit<ITestResult, 'id'> & { id: null };
