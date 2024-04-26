import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of, Subject } from 'rxjs';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';
import { STATUS_SUCCESS } from 'app/entities/scenario-execution/scenario-execution.model';

import ScenarioExecutionFilterComponent from './scenario-execution-filter.component';

const queryParamStartDate = new Date(2023, 10, 15).toISOString();
const queryParamEndDate = new Date(2023, 10, 16).toISOString();

const filterFormFromDate = '2023-11-15 00:00:00';
const filterFormToDate = '2023-11-16 00:00:00';

describe('ScenarioExecution Filter Component', () => {
  let router: Router;
  let activatedRoute: ActivatedRoute;

  let fixture: ComponentFixture<ScenarioExecutionFilterComponent>;
  let component: ScenarioExecutionFilterComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScenarioExecutionFilterComponent],
      providers: [
        {
          provide: Router,
          useValue: { navigate: jest.fn().mockReturnValueOnce(Promise.resolve()) },
        },
        {
          provide: ActivatedRoute,
          useValue: { queryParamMap: of(convertToParamMap({})), queryParams: of({}) },
        },
      ],
    })
      .overrideTemplate(ScenarioExecutionFilterComponent, '')
      .compileComponents();

    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture = TestBed.createComponent(ScenarioExecutionFilterComponent);
    component = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should initialize form values from activated route', () => {
      const nameContains = 'nameContains';

      // @ts-ignore: Access read-only property for testing
      activatedRoute.queryParamMap = of(
        convertToParamMap({
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
        }),
      );

      component.ngOnInit();

      expect(component.filterForm.getRawValue()).toEqual({
        fromDate: filterFormFromDate,
        nameContains,
        statusIn: STATUS_SUCCESS.name,
        toDate: filterFormToDate,
      });

      expect(component.filterForm.dirty).toBeTruthy();
    });

    it('should subscribe to form value changes', fakeAsync(() => {
      const filterFormValueChangesSubject = new Subject<{
        nameContains: string;
        statusIn: string;
        fromDate: string;
        toDate: string;
      }>();
      jest.spyOn(filterFormValueChangesSubject, 'subscribe');
      // @ts-ignore: Override read-only property for testing
      component.filterForm.valueChanges = filterFormValueChangesSubject;

      component.ngOnInit();

      // VERIFY that the subscription has been made

      // @ts-ignore: Access private property for testing
      expect(component.valueChanged).toBeFalsy();
      // @ts-ignore: Access private property for testing
      expect(component.filterFormValueChanges).not.toBeNull();
      expect(filterFormValueChangesSubject.subscribe).toHaveBeenCalled();

      expect(component.filterForm.getRawValue()).toEqual({ fromDate: null, nameContains: null, statusIn: null, toDate: null });

      // TRIGGER subscription and expect reload of data

      const nameContains = 'nameContains';
      filterFormValueChangesSubject.next({
        nameContains,
        statusIn: STATUS_SUCCESS.name,
        fromDate: filterFormFromDate,
        toDate: filterFormToDate,
      });

      tick(DEBOUNCE_TIME_MILLIS);

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
        },
      });
    }));
  });

  describe('ngOnDestroy', () => {
    it('should do noting if no subscription exists', () => {
      component.ngOnDestroy();
    });

    it('should unsubscribe from previous filterFormValueChanges', () => {
      const filterFormValueChanges = {
        unsubscribe: jest.fn(),
      };
      // @ts-ignore: access private property
      component.filterFormValueChanges = filterFormValueChanges;

      component.ngOnDestroy();

      expect(filterFormValueChanges.unsubscribe).toHaveBeenCalled();
    });
  });

  describe('applyFilter', () => {
    it('should navigate with correct query parameters', () => {
      const nameContains = 'nameContains';

      component.filterForm.setValue({
        nameContains,
        fromDate: filterFormFromDate,
        toDate: filterFormToDate,
        statusIn: STATUS_SUCCESS.name,
      });

      // @ts-ignore: Access protected function for testing
      component.applyFilter();

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
        },
      });
    });

    it('should ignore undefined parameters', () => {
      component.filterForm.controls['statusIn'].setValue(STATUS_SUCCESS.name);

      // @ts-ignore: Access protected function for testing
      component.applyFilter();

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          'filter[status.equals]': STATUS_SUCCESS.id,
        },
      });
    });

    it('should merge with existing query params', () => {
      const existing = 'some-value';
      activatedRoute.queryParams = of({ existing });

      component.filterForm.controls['statusIn'].setValue(STATUS_SUCCESS.name);

      // @ts-ignore: Access protected function for testing
      component.applyFilter();

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          existing,
          'filter[status.equals]': STATUS_SUCCESS.id,
        },
      });
    });

    it('undefined parameters override existing query params', () => {
      activatedRoute.queryParams = of({ 'filter[scenarioName.contains]': 'drama queen' });

      component.filterForm.controls['statusIn'].setValue(undefined);

      // @ts-ignore: Access protected function for testing
      component.applyFilter();

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {},
      });
    });
  });

  describe('resetFilter', () => {
    it('should reset form', () => {
      // Assume filter is dirty
      component.filterForm.markAsDirty();

      jest.spyOn(component.filterForm, 'reset');
      jest.spyOn(component.filterForm, 'markAsPristine');

      // @ts-ignore: Access protected function for testing
      component.resetFilter();

      expect(component.filterForm.reset).toHaveBeenCalled();
      expect(component.filterForm.markAsPristine).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalled();
    });
  });
});
