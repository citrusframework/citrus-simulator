import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITestParameter } from '../test-parameter.model';
import { TestParameterService } from '../service/test-parameter.service';

import testParameterResolve from './test-parameter-routing-resolve.service';

describe('TestParameter routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: TestParameterService;
  let resultTestParameter: ITestParameter | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting(), provideRouter([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(TestParameterService);
    resultTestParameter = undefined;
  });

  describe('resolve', () => {
    it('should return ITestParameter returned by find', () => {
      const expectedResult: ITestParameter = { key: 'key', testResult: { id: 123 } };

      // GIVEN
      service.find = jest.fn((testResultId, key) => of(new HttpResponse({ body: { key, testResult: { id: testResultId } } })));
      mockActivatedRouteSnapshot.params = { testResultId: expectedResult.testResult.id, key: expectedResult.key };

      // WHEN
      TestBed.runInInjectionContext(() => {
        testParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTestParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(expectedResult.testResult.id, expectedResult.key);
      expect(resultTestParameter).toEqual(expectedResult);
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        testParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTestParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultTestParameter).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITestParameter>({ body: null })));
      mockActivatedRouteSnapshot.params = { testResultId: 123, key: 'key' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        testParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTestParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123, 'key');
      expect(resultTestParameter).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
