import {
  scenarioExecutionStatusFromName,
  scenarioExecutionStatusFromId,
  STATUS_UNKNOWN,
  STATUS_RUNNING,
  STATUS_SUCCESS,
  STATUS_FAILED,
} from './scenario-execution.model';

describe('ScenarioExecutionStatus', () => {
  describe('scenarioExecutionStatusFromName', () => {
    it('should return the correct status for a valid name', () => {
      expect(scenarioExecutionStatusFromName('RUNNING')).toEqual(STATUS_RUNNING);
      expect(scenarioExecutionStatusFromName('SUCCESS')).toEqual(STATUS_SUCCESS);
      expect(scenarioExecutionStatusFromName('FAILED')).toEqual(STATUS_FAILED);
      expect(scenarioExecutionStatusFromName('UNKNOWN')).toEqual(STATUS_UNKNOWN);
    });

    it('should return STATUS_UNKNOWN for an invalid name', () => {
      expect(scenarioExecutionStatusFromName('invalid')).toEqual(STATUS_UNKNOWN);
    });
  });

  describe('scenarioExecutionStatusFromId', () => {
    it('should return the correct status for a valid id', () => {
      expect(scenarioExecutionStatusFromId(1)).toEqual(STATUS_RUNNING);
      expect(scenarioExecutionStatusFromId(2)).toEqual(STATUS_SUCCESS);
      expect(scenarioExecutionStatusFromId(3)).toEqual(STATUS_FAILED);
      expect(scenarioExecutionStatusFromId(0)).toEqual(STATUS_UNKNOWN);
    });

    it('should return STATUS_UNKNOWN for an invalid id', () => {
      expect(scenarioExecutionStatusFromId(99)).toEqual(STATUS_UNKNOWN);
    });
  });
});
