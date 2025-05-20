import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { IScenarioExecution } from '../scenario-execution.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../scenario-execution.test-samples';

import { RestScenarioExecution, ScenarioExecutionService } from './scenario-execution.service';

const requireRestSample: RestScenarioExecution = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('ScenarioExecution Service', () => {
  let service: ScenarioExecutionService;
  let httpMock: HttpTestingController;
  let expectedResult: IScenarioExecution | IScenarioExecution[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioExecutionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ScenarioExecution', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addScenarioExecutionToCollectionIfMissing', () => {
      it('should add a ScenarioExecution to an empty array', () => {
        const scenarioExecution: IScenarioExecution = sampleWithRequiredData;
        expectedResult = service.addScenarioExecutionToCollectionIfMissing([], scenarioExecution);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioExecution);
      });

      it('should not add a ScenarioExecution to an array that contains it', () => {
        const scenarioExecution: IScenarioExecution = sampleWithRequiredData;
        const scenarioExecutionCollection: IScenarioExecution[] = [
          {
            ...scenarioExecution,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScenarioExecutionToCollectionIfMissing(scenarioExecutionCollection, scenarioExecution);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScenarioExecution to an array that doesn't contain it", () => {
        const scenarioExecution: IScenarioExecution = sampleWithRequiredData;
        const scenarioExecutionCollection: IScenarioExecution[] = [sampleWithPartialData];
        expectedResult = service.addScenarioExecutionToCollectionIfMissing(scenarioExecutionCollection, scenarioExecution);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioExecution);
      });

      it('should add only unique ScenarioExecution to an array', () => {
        const scenarioExecutionArray: IScenarioExecution[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scenarioExecutionCollection: IScenarioExecution[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioExecutionToCollectionIfMissing(scenarioExecutionCollection, ...scenarioExecutionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenarioExecution: IScenarioExecution = sampleWithRequiredData;
        const scenarioExecution2: IScenarioExecution = sampleWithPartialData;
        expectedResult = service.addScenarioExecutionToCollectionIfMissing([], scenarioExecution, scenarioExecution2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioExecution);
        expect(expectedResult).toContain(scenarioExecution2);
      });

      it('should accept null and undefined values', () => {
        const scenarioExecution: IScenarioExecution = sampleWithRequiredData;
        expectedResult = service.addScenarioExecutionToCollectionIfMissing([], null, scenarioExecution, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioExecution);
      });

      it('should return initial array if no ScenarioExecution is added', () => {
        const scenarioExecutionCollection: IScenarioExecution[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioExecutionToCollectionIfMissing(scenarioExecutionCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioExecutionCollection);
      });
    });

    describe('compareScenarioExecution', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScenarioExecution(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { executionId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScenarioExecution(entity1, entity2);
        const compareResult2 = service.compareScenarioExecution(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { executionId: 123 };
        const entity2 = { executionId: 456 };

        const compareResult1 = service.compareScenarioExecution(entity1, entity2);
        const compareResult2 = service.compareScenarioExecution(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { executionId: 123 };
        const entity2 = { executionId: 123 };

        const compareResult1 = service.compareScenarioExecution(entity1, entity2);
        const compareResult2 = service.compareScenarioExecution(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
