import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

import { IScenarioAction } from '../scenario-action.model';
import { ScenarioActionService } from '../service/scenario-action.service';

import scenarioActionResolve from './scenario-action-routing-resolve.service';

describe('ScenarioAction routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ScenarioActionService;
  let resultScenarioAction: IScenarioAction | null | undefined;

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
    service = TestBed.inject(ScenarioActionService);
    resultScenarioAction = undefined;
  });

  describe('resolve', () => {
    it('should return IScenarioAction returned by find', () => {
      // GIVEN
      service.find = jest.fn(actionId => of(new HttpResponse({ body: { actionId } })));
      mockActivatedRouteSnapshot.params = { actionId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioActionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioAction = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioAction).toEqual({ actionId: 123 });
    });

    it('should return null if actionId is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioActionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioAction = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultScenarioAction).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IScenarioAction>({ body: null })));
      mockActivatedRouteSnapshot.params = { actionId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        scenarioActionResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultScenarioAction = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultScenarioAction).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
