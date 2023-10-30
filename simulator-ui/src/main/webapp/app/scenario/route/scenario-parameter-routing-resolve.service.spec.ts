import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

import { ScenarioService } from '../service/scenario.service';

import scenarioParameterResolve from './scenario-parameter-routing-resolve.service';

describe('ScenarioParameter routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ScenarioService;
  let resultParameters: IScenarioParameter[] | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
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
    service = TestBed.inject(ScenarioService);
    resultParameters = undefined;
  });

  describe('resolve', () => {
    it('should return IScenarioParameters returned by findParameters', () => {
      // GIVEN
      service.findParameters = jest.fn(name => of(new HttpResponse({ body: [{ parameterId: 123 }] })));
      mockActivatedRouteSnapshot.params = { name: 'test-scenario' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultParameters = result;
          },
        });
      });

      // THEN
      expect(service.findParameters).toBeCalledWith('test-scenario');
      expect(resultParameters).toEqual([{ parameterId: 123 }]);
    });

    it('should return null if name is not provided', () => {
      // GIVEN
      service.findParameters = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultParameters = result;
          },
        });
      });

      // THEN
      expect(service.findParameters).not.toBeCalled();
      expect(resultParameters).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'findParameters').mockReturnValue(of(new HttpResponse<IScenarioParameter[]>({ body: null })));
      mockActivatedRouteSnapshot.params = { name: 'test-scenario' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioParameterResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultParameters = result;
          },
        });
      });

      // THEN
      expect(service.findParameters).toBeCalledWith('test-scenario');
      expect(resultParameters).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
