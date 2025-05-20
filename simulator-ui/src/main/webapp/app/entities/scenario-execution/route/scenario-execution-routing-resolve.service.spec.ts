import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

import { IScenarioExecution } from '../scenario-execution.model';
import { ScenarioExecutionService } from '../service/scenario-execution.service';

import scenarioExecutionResolve from './scenario-execution-routing-resolve.service';

describe('ScenarioExecution routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ScenarioExecutionService;
  let resultScenarioExecution: IScenarioExecution | null | undefined;

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
    service = TestBed.inject(ScenarioExecutionService);
    resultScenarioExecution = undefined;
  });

  describe('resolve', () => {
    it('should return IScenarioExecution returned by find', () => {
      // GIVEN
      service.find = jest.fn(executionId => of(new HttpResponse({ body: { executionId } })));
      mockActivatedRouteSnapshot.params = { executionId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioExecutionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioExecution = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioExecution).toEqual({ executionId: 123 });
    });

    it('should return null if executionId is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioExecutionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioExecution = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultScenarioExecution).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IScenarioExecution>({ body: null })));
      mockActivatedRouteSnapshot.params = { executionId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioExecutionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioExecution = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioExecution).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
