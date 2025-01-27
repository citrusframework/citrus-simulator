import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { IScenarioAction } from '../scenario-action.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../scenario-action.test-samples';

import { RestScenarioAction, ScenarioActionService } from './scenario-action.service';

const requireRestSample: RestScenarioAction = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('ScenarioAction Service', () => {
  let service: ScenarioActionService;
  let httpMock: HttpTestingController;
  let expectedResult: IScenarioAction | IScenarioAction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioActionService);
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

    it('should return a list of ScenarioAction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addScenarioActionToCollectionIfMissing', () => {
      it('should add a ScenarioAction to an empty array', () => {
        const scenarioAction: IScenarioAction = sampleWithRequiredData;
        expectedResult = service.addScenarioActionToCollectionIfMissing([], scenarioAction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioAction);
      });

      it('should not add a ScenarioAction to an array that contains it', () => {
        const scenarioAction: IScenarioAction = sampleWithRequiredData;
        const scenarioActionCollection: IScenarioAction[] = [
          {
            ...scenarioAction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScenarioActionToCollectionIfMissing(scenarioActionCollection, scenarioAction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScenarioAction to an array that doesn't contain it", () => {
        const scenarioAction: IScenarioAction = sampleWithRequiredData;
        const scenarioActionCollection: IScenarioAction[] = [sampleWithPartialData];
        expectedResult = service.addScenarioActionToCollectionIfMissing(scenarioActionCollection, scenarioAction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioAction);
      });

      it('should add only unique ScenarioAction to an array', () => {
        const scenarioActionArray: IScenarioAction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scenarioActionCollection: IScenarioAction[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioActionToCollectionIfMissing(scenarioActionCollection, ...scenarioActionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenarioAction: IScenarioAction = sampleWithRequiredData;
        const scenarioAction2: IScenarioAction = sampleWithPartialData;
        expectedResult = service.addScenarioActionToCollectionIfMissing([], scenarioAction, scenarioAction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioAction);
        expect(expectedResult).toContain(scenarioAction2);
      });

      it('should accept null and undefined values', () => {
        const scenarioAction: IScenarioAction = sampleWithRequiredData;
        expectedResult = service.addScenarioActionToCollectionIfMissing([], null, scenarioAction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioAction);
      });

      it('should return initial array if no ScenarioAction is added', () => {
        const scenarioActionCollection: IScenarioAction[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioActionToCollectionIfMissing(scenarioActionCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioActionCollection);
      });
    });

    describe('compareScenarioAction', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScenarioAction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { actionId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScenarioAction(entity1, entity2);
        const compareResult2 = service.compareScenarioAction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { actionId: 123 };
        const entity2 = { actionId: 456 };

        const compareResult1 = service.compareScenarioAction(entity1, entity2);
        const compareResult2 = service.compareScenarioAction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { actionId: 123 };
        const entity2 = { actionId: 123 };

        const compareResult1 = service.compareScenarioAction(entity1, entity2);
        const compareResult2 = service.compareScenarioAction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
