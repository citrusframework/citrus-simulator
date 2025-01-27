import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { sampleWithRequiredData as sampleParameterWithRequiredData } from 'app/entities/scenario-parameter/scenario-parameter.test-samples';
import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

import { IScenario } from '../scenario.model';
import { ScenarioService } from './scenario.service';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../scenario.test-samples';

describe('Scenario Service', () => {
  let service: ScenarioService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });

    service = TestBed.inject(ScenarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find parameters', () => {
      const returnedFromService: IScenarioParameter[] = [sampleParameterWithRequiredData];

      let expectedResult: IScenarioParameter[] | null = null;
      service.findParameters('scenario-name').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);

      expect(expectedResult).toMatchObject(returnedFromService);
    });

    it('should launch a Scenario', () => {
      const returnedFromService = 1234;
      const scenarioName = 'scenario-name';
      const scenarioParameters: IScenarioParameter[] = [];

      let expectedResult: number | null = null;
      service.launch(scenarioName, scenarioParameters).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);

      expect(expectedResult).toEqual(returnedFromService);
    });

    describe('addScenarioToCollectionIfMissing', () => {
      let expectedResult: IScenario | IScenario[] | boolean | null;

      beforeEach(() => {
        expectedResult = null;
      });

      it('should add a Scenario to an empty array', () => {
        const scenario: IScenario = sampleWithRequiredData;
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should not add a Scenario to an array that contains it', () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenarioCollection: IScenario[] = [
          {
            ...scenario,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Scenario to an array that doesn't contain it", () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenarioCollection: IScenario[] = [sampleWithPartialData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
      });

      it('should add only unique Scenario to an array', () => {
        const scenarioArray: IScenario[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scenarioCollection: IScenario[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, ...scenarioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenario2: IScenario = sampleWithPartialData;
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario, scenario2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
        expect(expectedResult).toContain(scenario2);
      });

      it('should accept null and undefined values', () => {
        const scenario: IScenario = sampleWithRequiredData;
        expectedResult = service.addScenarioToCollectionIfMissing([], null, scenario, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should return initial array if no Scenario is added', () => {
        const scenarioCollection: IScenario[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioCollection);
      });
    });

    describe('compareScenario', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScenario(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { name: 'name' };
        const entity2 = null;

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { name: 'name' };
        const entity2 = { name: 'another-name' };

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { name: 'name' };
        const entity2 = { name: 'name' };

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
