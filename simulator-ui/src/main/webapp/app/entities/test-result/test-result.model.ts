import dayjs from 'dayjs/esm';

export interface ITestResult {
  id: number;
  status?: 'UNKNOWN' | 'SUCCESS' | 'FAILURE' | 'SKIP' | null;
  testName?: string | null;
  className?: string | null;
  errorMessage?: string | null;
  stackTrace?: string | null;
  failureType?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export interface ITestResultStatus {
  id: number;
  name: 'UNKNOWN' | 'SUCCESS' | 'FAILURE' | 'SKIP';
}

export const STATUS_UNKNOWN: ITestResultStatus = { id: 0, name: 'UNKNOWN' };
export const STATUS_SUCCESS: ITestResultStatus = { id: 1, name: 'SUCCESS' };
export const STATUS_FAILURE: ITestResultStatus = { id: 2, name: 'FAILURE' };
export const STATUS_SKIP: ITestResultStatus = { id: 3, name: 'SKIP' };

const ALL_STATUS = [STATUS_UNKNOWN, STATUS_SUCCESS, STATUS_FAILURE, STATUS_SKIP];

export const testResultStatusFromName = (name: string): ITestResultStatus => ALL_STATUS.find(v => v.name === name) ?? STATUS_UNKNOWN;

export const testResultStatusFromId = (id: number): ITestResultStatus => ALL_STATUS.find(v => v.id === id) ?? STATUS_UNKNOWN;
