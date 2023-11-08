import dayjs from 'dayjs/esm';

export interface ITestResult {
  id: number;
  status?: 'UNKNOWN' | 'SUCCESS' | 'FAILURE' | 'SKIP' | null;
  testName?: string | null;
  className?: string | null;
  errorMessage?: string | null;
  failureStack?: string | null;
  failureType?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewTestResult = Omit<ITestResult, 'id'> & { id: null };

export interface ITestResultStatus {
  id: number;
  name: 'UNKNOWN' | 'SUCCESS' | 'FAILURE' | 'SKIP';
}

export const STATUS_UNKNOWN: ITestResultStatus = { id: 0, name: 'UNKNOWN' };
export const STATUS_SUCCESS: ITestResultStatus = { id: 1, name: 'SUCCESS' };
export const STATUS_FAILED: ITestResultStatus = { id: 2, name: 'FAILURE' };
export const STATUS_SKIP: ITestResultStatus = { id: 3, name: 'SKIP' };
