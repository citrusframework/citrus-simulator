import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITestParameter } from '../test-parameter.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../test-parameter.test-samples';

import { RestTestParameter, TestParameterService } from './test-parameter.service';

const requireRestSample: RestTestParameter = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('TestParameter Service', () => {
  let service: TestParameterService;
  let httpMock: HttpTestingController;
  let expectedResult: ITestParameter | ITestParameter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TestParameterService);
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

    it('should return a list of TestParameter', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addTestParameterToCollectionIfMissing', () => {
      it('should add a TestParameter to an empty array', () => {
        const testParameter: ITestParameter = sampleWithRequiredData;
        expectedResult = service.addTestParameterToCollectionIfMissing([], testParameter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testParameter);
      });

      it('should not add a TestParameter to an array that contains it', () => {
        const testParameter: ITestParameter = sampleWithRequiredData;
        const testParameterCollection: ITestParameter[] = [
          {
            ...testParameter,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTestParameterToCollectionIfMissing(testParameterCollection, testParameter);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TestParameter to an array that doesn't contain it", () => {
        const testParameter: ITestParameter = sampleWithRequiredData;
        const testParameterCollection: ITestParameter[] = [sampleWithPartialData];
        expectedResult = service.addTestParameterToCollectionIfMissing(testParameterCollection, testParameter);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testParameter);
      });

      it('should add only unique TestParameter to an array', () => {
        const testParameterArray: ITestParameter[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const testParameterCollection: ITestParameter[] = [sampleWithRequiredData];
        expectedResult = service.addTestParameterToCollectionIfMissing(testParameterCollection, ...testParameterArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const testParameter: ITestParameter = sampleWithRequiredData;
        const testParameter2: ITestParameter = sampleWithPartialData;
        expectedResult = service.addTestParameterToCollectionIfMissing([], testParameter, testParameter2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testParameter);
        expect(expectedResult).toContain(testParameter2);
      });

      it('should accept null and undefined values', () => {
        const testParameter: ITestParameter = sampleWithRequiredData;
        expectedResult = service.addTestParameterToCollectionIfMissing([], null, testParameter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testParameter);
      });

      it('should return initial array if no TestParameter is added', () => {
        const testParameterCollection: ITestParameter[] = [sampleWithRequiredData];
        expectedResult = service.addTestParameterToCollectionIfMissing(testParameterCollection, undefined, null);
        expect(expectedResult).toEqual(testParameterCollection);
      });
    });

    describe('compareTestParameter', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTestParameter(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3696 };
        const entity2 = null;

        const compareResult1 = service.compareTestParameter(entity1, entity2);
        const compareResult2 = service.compareTestParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3696 };
        const entity2 = { id: 10882 };

        const compareResult1 = service.compareTestParameter(entity1, entity2);
        const compareResult2 = service.compareTestParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 3696 };
        const entity2 = { id: 3696 };

        const compareResult1 = service.compareTestParameter(entity1, entity2);
        const compareResult2 = service.compareTestParameter(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
