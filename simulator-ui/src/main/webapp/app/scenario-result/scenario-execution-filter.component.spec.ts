import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { of } from 'rxjs';

import ScenarioExecutionFilterComponent from './scenario-execution-filter.component';
import { STATUS_SUCCESS } from '../entities/scenario-execution/scenario-execution.model';

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
          useValue: { queryParamMap: of(convertToParamMap({})) },
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

  it('should initialize form values from activated route on ngOnInit', () => {
    const nameContains = 'testName';

    // @ts-ignore: Access read-only property for testing
    activatedRoute.queryParamMap = of(
      convertToParamMap({
        'filter[scenarioName.contains]': nameContains,
        'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
        'filter[status.in]': STATUS_SUCCESS.id,
        'filter[endDate.lessThanOrEqual]': queryParamEndDate,
      }),
    );

    component.ngOnInit();

    expect(component.filterForm.controls.nameContains.value).toEqual(nameContains);
    expect(component.filterForm.controls.fromDate.value).toEqual(filterFormFromDate);
    expect(component.filterForm.controls.toDate.value).toEqual(filterFormToDate);
    expect(component.filterForm.controls.statusIn.value).toEqual(STATUS_SUCCESS.name);

    expect(component.filterForm.dirty).toBeTruthy();
    expect(component.valueChanged).toBeFalsy();
  });

  it('should navigate with correct query parameters on applyFilter', () => {
    const nameContains = 'testName';

    component.valueChanged = true;
    component.filterForm.setValue({
      nameContains,
      fromDate: filterFormFromDate,
      toDate: filterFormToDate,
      statusIn: STATUS_SUCCESS.name,
    });

    component.applyFilter();

    expect(router.navigate).toHaveBeenCalledWith([], {
      queryParams: {
        'filter[scenarioName.contains]': nameContains,
        'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
        'filter[status.in]': STATUS_SUCCESS.id,
        'filter[endDate.lessThanOrEqual]': queryParamEndDate,
      },
    });
    expect(component.valueChanged).toBeFalsy();
  });

  describe('resetFilter', () => {
    it('should reset form', () => {
      // Assume filter is dirty
      component.valueChanged = true;
      component.filterForm.markAsDirty();

      jest.spyOn(component.filterForm, 'reset');
      jest.spyOn(component.filterForm, 'markAsPristine');

      component.resetFilter();

      expect(component.filterForm.reset).toHaveBeenCalled();
      expect(component.filterForm.markAsPristine).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalled();
      expect(component.valueChanged).toBeFalsy();
    });

    it('should unsubscribe from previous filterFormValueChanges', () => {
      const filterFormValueChanges = {
        unsubscribe: jest.fn(),
      };
      // @ts-ignore: access private property
      component.filterFormValueChanges = filterFormValueChanges;

      component.resetFilter();

      expect(filterFormValueChanges.unsubscribe).toHaveBeenCalled();
    });
  });
});
