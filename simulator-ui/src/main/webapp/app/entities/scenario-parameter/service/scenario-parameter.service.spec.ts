import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { IScenarioParameter } from '../scenario-parameter.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../scenario-parameter.test-samples';

import { RestScenarioParameter, ScenarioParameterService } from './scenario-parameter.service';

const requireRestSample: RestScenarioParameter = {
  ...sampleWithRequiredData,
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('ScenarioParameter Service', () => {
  let service: ScenarioParameterService;
  let httpMock: HttpTestingController;
  let expectedResult: IScenarioParameter | IScenarioParameter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioParameterService);
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

    it('should return a list of ScenarioParameter', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addScenarioParameterToCollectionIfMissing', () => {
      it('should add a ScenarioParameter to an empty array', () => {
        const scenarioParameter: IScenarioParameter = sampleWithRequiredData;
        expectedResult = service.addScenarioParameterToCollectionIfMissing([], scenarioParameter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioParameter);
      });

      it('should not add a ScenarioParameter to an array that contains it', () => {
        const scenarioParameter: IScenarioParameter = sampleWithRequiredData;
        const scenarioParameterCollection: IScenarioParameter[] = [
          {
            ...scenarioParameter,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScenarioParameterToCollectionIfMissing(scenarioParameterCollection, scenarioParameter);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScenarioParameter to an array that doesn't contain it", () => {
        const scenarioParameter: IScenarioParameter = sampleWithRequiredData;
        const scenarioParameterCollection: IScenarioParameter[] = [sampleWithPartialData];
        expectedResult = service.addScenarioParameterToCollectionIfMissing(scenarioParameterCollection, scenarioParameter);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioParameter);
      });

      it('should add only unique ScenarioParameter to an array', () => {
        const scenarioParameterArray: IScenarioParameter[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scenarioParameterCollection: IScenarioParameter[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioParameterToCollectionIfMissing(scenarioParameterCollection, ...scenarioParameterArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenarioParameter: IScenarioParameter = sampleWithRequiredData;
        const scenarioParameter2: IScenarioParameter = sampleWithPartialData;
        expectedResult = service.addScenarioParameterToCollectionIfMissing([], scenarioParameter, scenarioParameter2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenarioParameter);
        expect(expectedResult).toContain(scenarioParameter2);
      });

      it('should accept null and undefined values', () => {
        const scenarioParameter: IScenarioParameter = sampleWithRequiredData;
        expectedResult = service.addScenarioParameterToCollectionIfMissing([], null, scenarioParameter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenarioParameter);
      });

      it('should return initial array if no ScenarioParameter is added', () => {
        const scenarioParameterCollection: IScenarioParameter[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioParameterToCollectionIfMissing(scenarioParameterCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioParameterCollection);
      });
    });

    describe('compareScenarioParameter', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScenarioParameter(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { parameterId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScenarioParameter(entity1, entity2);
        const compareResult2 = service.compareScenarioParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { parameterId: 123 };
        const entity2 = { parameterId: 456 };

        const compareResult1 = service.compareScenarioParameter(entity1, entity2);
        const compareResult2 = service.compareScenarioParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { parameterId: 123 };
        const entity2 = { parameterId: 123 };

        const compareResult1 = service.compareScenarioParameter(entity1, entity2);
        const compareResult2 = service.compareScenarioParameter(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
