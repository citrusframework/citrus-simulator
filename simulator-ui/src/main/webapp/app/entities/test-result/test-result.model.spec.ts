import {
  testResultStatusFromId,
  testResultStatusFromName,
  STATUS_UNKNOWN,
  STATUS_SUCCESS,
  STATUS_FAILURE,
  STATUS_SKIP,
  STATUS_RUNNING,
} from './test-result.model';

describe('TestResult Status', () => {
  describe('testResultStatusFromName', () => {
    it('should return the correct status for a valid name', () => {
      expect(testResultStatusFromName('UNKNOWN')).toEqual(STATUS_UNKNOWN);
      expect(testResultStatusFromName('RUNNING')).toEqual(STATUS_RUNNING);
      expect(testResultStatusFromName('SUCCESS')).toEqual(STATUS_SUCCESS);
      expect(testResultStatusFromName('FAILURE')).toEqual(STATUS_FAILURE);
      expect(testResultStatusFromName('SKIP')).toEqual(STATUS_SKIP);
    });

    it('should return STATUS_UNKNOWN for an invalid name', () => {
      expect(testResultStatusFromName('invalid')).toEqual(STATUS_UNKNOWN);
    });
  });

  describe('testResultStatusFromId', () => {
    it('should return the correct status for a valid id', () => {
      expect(testResultStatusFromId(0)).toEqual(STATUS_UNKNOWN);
      expect(testResultStatusFromId(-1)).toEqual(STATUS_RUNNING);
      expect(testResultStatusFromId(1)).toEqual(STATUS_SUCCESS);
      expect(testResultStatusFromId(2)).toEqual(STATUS_FAILURE);
      expect(testResultStatusFromId(3)).toEqual(STATUS_SKIP);
    });

    it('should return STATUS_UNKNOWN for an invalid id', () => {
      expect(testResultStatusFromId(99)).toEqual(STATUS_UNKNOWN);
    });
  });
});
