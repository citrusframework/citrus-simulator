import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

import { IScenarioParameter } from '../scenario-parameter.model';
import { ScenarioParameterService } from '../service/scenario-parameter.service';

import scenarioParameterResolve from './scenario-parameter-routing-resolve.service';

describe('ScenarioParameter routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ScenarioParameterService;
  let resultScenarioParameter: IScenarioParameter | null | undefined;

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
    service = TestBed.inject(ScenarioParameterService);
    resultScenarioParameter = undefined;
  });

  describe('resolve', () => {
    it('should return IScenarioParameter returned by find', () => {
      // GIVEN
      service.find = jest.fn(parameterId => of(new HttpResponse({ body: { parameterId } })));
      mockActivatedRouteSnapshot.params = { parameterId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioParameter).toEqual({ parameterId: 123 });
    });

    it('should return null if parameterId is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultScenarioParameter).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IScenarioParameter>({ body: null })));
      mockActivatedRouteSnapshot.params = { parameterId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioParameter = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioParameter).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
