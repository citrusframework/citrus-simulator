import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

import { Observable, of, Subject } from 'rxjs';

import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { TranslateModule } from '@ngx-translate/core';

import dayjs from 'dayjs/esm';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';

import { STATUS_SUCCESS } from 'app/entities/test-result/test-result.model';

import SharedModule from 'app/shared/shared.module';

import ScenarioExecutionFilterComponent, {
  headerFilterFormToString,
  headerFilterStringToForm,
  invalidHeaderFilterPatternValidator,
} from './scenario-execution-filter.component';

import HeaderFilterHelpDialogComponent from './header-filter-help-dialog.component';
import HeaderFilterDialogComponent, { ComparatorType, HeaderFilter, ValueType } from './header-filter-dialog.component';

const queryParamStartDate = new Date(2023, 10, 15).toISOString();
const queryParamEndDate = new Date(2023, 10, 16).toISOString();

const filterFormFromDate = '2023-11-15 00:00:00';
const filterFormToDate = '2023-11-16 00:00:00';

const keyComparator = new FormControl<ComparatorType>({ value: ComparatorType.EQUALS, disabled: true });
const valueType = new FormControl<ValueType>(ValueType.LITERAL);

class MockNgbModalRef {
  componentInstance = { headerFilters: [] };
  closed: Observable<any> = new Subject();
}

describe('ScenarioExecution Filter Component', () => {
  let activatedRoute: ActivatedRoute;
  let router: Router;

  let mockModalRef: NgbModalRef;

  let fixture: ComponentFixture<ScenarioExecutionFilterComponent>;
  let component: ScenarioExecutionFilterComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule, TranslateModule.forRoot(), ScenarioExecutionFilterComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { queryParamMap: of(convertToParamMap({})), queryParams: of({}) },
        },
        {
          provide: Router,
          useValue: { navigate: jest.fn().mockReturnValueOnce(Promise.resolve()) },
        },
      ],
    })
      .overrideTemplate(ScenarioExecutionFilterComponent, '')
      .compileComponents();

    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(ScenarioExecutionFilterComponent);
    component = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should initialize form values from activated route', () => {
      const nameContains = 'nameContains';
      const headerFilter = 'header%3Dvalue';

      // @ts-expect-error: Access read-only property for testing
      activatedRoute.queryParamMap = of(
        convertToParamMap({
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
          'filter[headers]': headerFilter,
        }),
      );

      dayjs.utc = jest.fn().mockReturnValueOnce(dayjs(queryParamStartDate)).mockReturnValueOnce(dayjs(queryParamEndDate));

      component.ngOnInit();

      expect(component.filterForm.getRawValue()).toEqual({
        fromDate: filterFormFromDate,
        nameContains,
        statusIn: STATUS_SUCCESS.name,
        toDate: filterFormToDate,
        headerFilter,
      });

      expect(component.filterForm.dirty).toBeTruthy();
    });

    it('should subscribe to form value changes', fakeAsync(() => {
      const filterFormValueChangesSubject = new Subject<{
        nameContains: string;
        statusIn: string;
        fromDate: string;
        toDate: string;
        headerFilter: string;
      }>();
      jest.spyOn(filterFormValueChangesSubject, 'subscribe');
      // @ts-expect-error: Override read-only property for testing
      component.filterForm.valueChanges = filterFormValueChangesSubject;

      component.ngOnInit();

      // VERIFY that the subscription has been made

      // @ts-expect-error: Access private property for testing
      expect(component.valueChanged).toBeFalsy();
      // @ts-expect-error: Access private property for testing
      expect(component.filterFormValueChanges).not.toBeNull();
      // eslint-disable-next-line @typescript-eslint/no-deprecated
      expect(filterFormValueChangesSubject.subscribe).toHaveBeenCalled();

      expect(component.filterForm.getRawValue()).toEqual({
        fromDate: null,
        headerFilter: '',
        nameContains: '',
        statusIn: null,
        toDate: null,
      });

      // TRIGGER subscription and expect reload of data

      const nameContains = 'nameContains';
      const headerFilter = 'key=value';
      filterFormValueChangesSubject.next({
        nameContains,
        statusIn: STATUS_SUCCESS.name,
        fromDate: filterFormFromDate,
        toDate: filterFormToDate,
        headerFilter,
      });

      dayjs.utc = jest.fn().mockReturnValueOnce(dayjs(filterFormFromDate)).mockReturnValueOnce(dayjs(filterFormToDate));

      tick(DEBOUNCE_TIME_MILLIS);

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
          'filter[headers]': headerFilter,
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
      // @ts-expect-error: access private property
      component.filterFormValueChanges = filterFormValueChanges;

      component.ngOnDestroy();

      expect(filterFormValueChanges.unsubscribe).toHaveBeenCalled();
    });
  });

  describe('applyFilter', () => {
    it('should navigate with correct query parameters', () => {
      const nameContains = 'nameContains';
      const headerFilter = 'headerFilter';

      component.filterForm.setValue({
        nameContains,
        fromDate: filterFormFromDate,
        toDate: filterFormToDate,
        statusIn: STATUS_SUCCESS.name,
        headerFilter,
      });

      dayjs.utc = jest.fn().mockReturnValueOnce(dayjs(filterFormFromDate)).mockReturnValueOnce(dayjs(filterFormToDate));

      // @ts-expect-error: Access protected function for testing
      component.applyFilter();

      expect(router.navigate).toHaveBeenCalledWith([], {
        queryParams: {
          'filter[scenarioName.contains]': nameContains,
          'filter[startDate.greaterThanOrEqual]': queryParamStartDate,
          'filter[status.equals]': STATUS_SUCCESS.id,
          'filter[endDate.lessThanOrEqual]': queryParamEndDate,
          'filter[headers]': headerFilter,
        },
      });
    });

    it('should ignore undefined parameters', () => {
      component.filterForm.get('statusIn')?.setValue(STATUS_SUCCESS.name);

      // @ts-expect-error: Access protected function for testing
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

      component.filterForm.get('statusIn')?.setValue(STATUS_SUCCESS.name);

      // @ts-expect-error: Access protected function for testing
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

      component.filterForm.get('statusIn')?.setValue(undefined);

      // @ts-expect-error: Access protected function for testing
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

      component.resetFilter();

      expect(component.filterForm.reset).toHaveBeenCalled();
      expect(component.filterForm.markAsPristine).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalled();
    });
  });

  describe('openHelpModal', () => {
    it('opens the header filter help dialog', () => {
      mockModalRef = new MockNgbModalRef() as unknown as NgbModalRef;
      jest.spyOn(component.modalService, 'open').mockReturnValueOnce(mockModalRef);

      component.openHelpModal();

      expect(component.modalService.open).toHaveBeenCalledWith(HeaderFilterHelpDialogComponent, { size: 'm' });
    });
  });

  describe('openHeaderFilterModal', () => {
    beforeEach(() => {
      mockModalRef = new MockNgbModalRef() as unknown as NgbModalRef;
      jest.spyOn(component.modalService, 'open').mockReturnValueOnce(mockModalRef);
    });

    it('opens the header filter dialog', () => {
      component.openHeaderFilterModal();

      expect(component.modalService.open).toHaveBeenCalledWith(HeaderFilterDialogComponent, { backdrop: 'static', size: 'xl' });

      expect(mockModalRef.componentInstance.headerFilters).toHaveLength(0);
    });

    it('opens the header filter dialog with initial data', () => {
      component.filterForm.get('headerFilter')?.setValue('key=value');

      component.openHeaderFilterModal();

      expect(component.modalService.open).toHaveBeenCalledWith(HeaderFilterDialogComponent, { backdrop: 'static', size: 'xl' });

      expect(mockModalRef.componentInstance.headerFilters).toHaveLength(1);
      expect(mockModalRef.componentInstance.headerFilters[0].getRawValue()).toEqual({
        key: 'key',
        keyComparator: '=',
        value: 'value',
        valueComparator: '=',
        valueType: 'LITERAL',
      });
    });

    it('opens the header filter dialog with multiple values', () => {
      // Note the double space for the second key
      component.filterForm.get('headerFilter')?.setValue('key=value; key=value');

      component.openHeaderFilterModal();

      expect(component.modalService.open).toHaveBeenCalledWith(HeaderFilterDialogComponent, { backdrop: 'static', size: 'xl' });

      expect(mockModalRef.componentInstance.headerFilters).toHaveLength(2);
      expect(mockModalRef.componentInstance.headerFilters[0].getRawValue()).toEqual({
        key: 'key',
        keyComparator: '=',
        value: 'value',
        valueComparator: '=',
        valueType: 'LITERAL',
      });
      expect(mockModalRef.componentInstance.headerFilters[1].getRawValue()).toEqual({
        key: 'key',
        keyComparator: '=',
        value: 'value',
        valueComparator: '=',
        valueType: 'LITERAL',
      });
    });
  });
});

describe('invalidHeaderFilterPatternValidator', () => {
  it.each([
    // Made-up values-only
    'header',
    'header with spaces',
    'snake_case',
    'b1028a4e-df33-40b6-a5aa-abbec001606a',
    // Real word samples
    'accept-encoding=gzip, x-gzip, deflate',
    'citrus_endpoint_uri=/services/rest/simulator',
    'citrus_http_version=HTTP/1.1',
    'connection=keep-alive',
    'contentType=application/xml;charset=UTF-8',
    'host=localhost:8080',
    'user-agent=Apache-HttpClient/5.2.3 (Java/17.0.11)',
    // Made-up key-value pairs
    'key=',
    'key=value',
    'kebab-case=value',
    'snake_case=value',
    'key=b1028a4e-df33-40b6-a5aa-abbec001606a',
    'key=snake_case',
    'key~value',
    'key<1234',
    'key>2345',
    'key>=3456',
    'key<=4567',
  ])('returns null on valid pattern: %s', (pattern: string) => {
    expect(invalidHeaderFilterPatternValidator()({ value: pattern } as AbstractControl)).toBeNull();
  });

  it.each(['key<foo', 'key>bar', 'key>=baz', 'key<=boom', ' key-starting-with-space', 'key with spaces=value'])(
    'returns error information on invalid pattern: %s',
    (pattern: string) => {
      expect(invalidHeaderFilterPatternValidator()({ value: pattern } as AbstractControl)).toEqual({
        invalidHeaderFilterPattern: { value: pattern },
      });
    },
  );
});

describe('headerFilterFormToString', () => {
  it.each([
    ['foo', '1', ComparatorType.EQUALS, 'foo=1'],
    ['foo', '2', ComparatorType.CONTAINS, 'foo~2'],
    ['bar', '3', ComparatorType.GREATER_THAN, 'bar>3'],
    ['bar', '4', ComparatorType.GREATER_THAN_OR_EQUAL_TO, 'bar>=4'],
    ['baz', '5', ComparatorType.LESS_THAN, 'baz<5'],
    ['baz', '6', ComparatorType.LESS_THAN_OR_EQUAL_TO, 'baz<=6'],
    ['', 'value only', ComparatorType.EQUALS, 'value only'],
    [' key starting with space', 'value only', ComparatorType.EQUALS, ' key starting with space=value only'],
    ['key', ' value starting with space', ComparatorType.EQUALS, 'key= value starting with space'],
  ])(
    'derives a filter string literal from the form group',
    (key: string, value: string, comparatorType: ComparatorType, expected: string) => {
      const formGroup = new FormGroup<HeaderFilter>({
        key: new FormControl<string>(key),
        keyComparator,
        value: new FormControl<string>(value.toString()),
        valueType,
        valueComparator: new FormControl<ComparatorType>(comparatorType),
      });

      const result = headerFilterFormToString(formGroup);

      expect(result).toEqual(expected);
    },
  );
});

describe('headerFilterStringToForm', () => {
  it.each([
    ['header', { key: '', value: 'header', valueComparator: ComparatorType.EQUALS }],
    ['header with spaces', { key: '', value: 'header with spaces', valueComparator: ComparatorType.EQUALS }],
    ['key=', { key: 'key', value: '', valueComparator: ComparatorType.EQUALS }],
    ['key=value', { key: 'key', value: 'value', valueComparator: ComparatorType.EQUALS }],
    ['key= header starting with space', { key: 'key', value: ' header starting with space', valueComparator: ComparatorType.EQUALS }],
    ['key~value', { key: 'key', value: 'value', valueComparator: ComparatorType.CONTAINS }],
    ['key<1234', { key: 'key', value: '1234', valueComparator: ComparatorType.LESS_THAN }],
    ['key>2345', { key: 'key', value: '2345', valueComparator: ComparatorType.GREATER_THAN }],
    ['key>=3456', { key: 'key', value: '3456', valueComparator: ComparatorType.GREATER_THAN_OR_EQUAL_TO }],
    ['key<=4567', { key: 'key', value: '4567', valueComparator: ComparatorType.LESS_THAN_OR_EQUAL_TO }],
  ])('derives a form group from valid string literal: %s', (input: string, expectedResult) => {
    const result = headerFilterStringToForm(input);

    expect(result).toBeTruthy();
    expect((result as FormGroup<HeaderFilter>).get('key')?.value).toEqual(expectedResult.key);
    expect((result as FormGroup<HeaderFilter>).get('keyComparator')?.value).toEqual(ComparatorType.EQUALS);
    expect((result as FormGroup<HeaderFilter>).get('value')?.value).toEqual(expectedResult.value);
    expect((result as FormGroup<HeaderFilter>).get('valueType')?.value).toEqual(ValueType.LITERAL);
    expect((result as FormGroup<HeaderFilter>).get('valueComparator')?.value).toEqual(expectedResult.valueComparator);
  });

  it.each([
    'key<foo',
    'key>bar',
    'key>=baz',
    'key<=boom',
    'key< 1234',
    'key> 1234',
    'key<= 1234',
    'key>= 1234',
    'contentType=application/xml;charset=UTF-8',
  ])('returns false on invalid pattern', (pattern: string) => {
    expect(headerFilterStringToForm(pattern)).toBeFalsy();
  });
});
