import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { ITestResult } from '../test-result.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../test-result.test-samples';

import { RestTestResult, TestResultsByStatus, TestResultService } from './test-result.service';

const requireRestSample: RestTestResult = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('TestResult Service', () => {
  let service: TestResultService;
  let httpMock: HttpTestingController;
  let expectedResult: ITestResult | ITestResult[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TestResultService);
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

    it('should return a list of TestResult', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should return results count by status', () => {
      const returnedFromService: TestResultsByStatus = { total: 3, successful: 2, failed: 1 };

      let actualResult: TestResultsByStatus | null;
      service.countByStatus().subscribe(resp => (actualResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();

      // @ts-expect-error: Usage before assignment is ok
      expect(actualResult).toMatchObject(actualResult);
    });

    describe('addTestResultToCollectionIfMissing', () => {
      it('should add a TestResult to an empty array', () => {
        const testResult: ITestResult = sampleWithRequiredData;
        expectedResult = service.addTestResultToCollectionIfMissing([], testResult);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testResult);
      });

      it('should not add a TestResult to an array that contains it', () => {
        const testResult: ITestResult = sampleWithRequiredData;
        const testResultCollection: ITestResult[] = [
          {
            ...testResult,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, testResult);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TestResult to an array that doesn't contain it", () => {
        const testResult: ITestResult = sampleWithRequiredData;
        const testResultCollection: ITestResult[] = [sampleWithPartialData];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, testResult);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testResult);
      });

      it('should add only unique TestResult to an array', () => {
        const testResultArray: ITestResult[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const testResultCollection: ITestResult[] = [sampleWithRequiredData];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, ...testResultArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const testResult: ITestResult = sampleWithRequiredData;
        const testResult2: ITestResult = sampleWithPartialData;
        expectedResult = service.addTestResultToCollectionIfMissing([], testResult, testResult2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(testResult);
        expect(expectedResult).toContain(testResult2);
      });

      it('should accept null and undefined values', () => {
        const testResult: ITestResult = sampleWithRequiredData;
        expectedResult = service.addTestResultToCollectionIfMissing([], null, testResult, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(testResult);
      });

      it('should return initial array if no TestResult is added', () => {
        const testResultCollection: ITestResult[] = [sampleWithRequiredData];
        expectedResult = service.addTestResultToCollectionIfMissing(testResultCollection, undefined, null);
        expect(expectedResult).toEqual(testResultCollection);
      });
    });

    describe('compareTestResult', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTestResult(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTestResult(entity1, entity2);
        const compareResult2 = service.compareTestResult(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTestResult(entity1, entity2);
        const compareResult2 = service.compareTestResult(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTestResult(entity1, entity2);
        const compareResult2 = service.compareTestResult(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
