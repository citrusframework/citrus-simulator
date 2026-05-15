import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { EMPTY, of, Subject, throwError } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

import { SortOrder, sortStateSignal } from 'app/shared/sort';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { AlertService } from 'app/core/util/alert.service';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

jest.mock('app/core/util/alert.service');

import SpyInstance = jest.SpyInstance;

describe('Scenario Management Component', () => {
  let activatedRoute: ActivatedRoute;
  let alertService: AlertService;
  let service: ScenarioService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;
  let userPreferenceService: UserPreferenceService;

  let fixture: ComponentFixture<ScenarioComponent>;
  let component: ScenarioComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScenarioComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([{ path: 'scenario', component: ScenarioComponent }]),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            snapshot: { queryParams: {} },
          },
        },
        AlertService,
        {
          provide: UserPreferenceService,
          useValue: {
            getPageSize: jest.fn(),
            getSortState: jest.fn().mockReturnValue(sortStateSignal({ predicate: 'id', order: SortOrder.ASCENDING })),
            setSortState: jest.fn(),
          },
        },
      ],
    })
      .overrideTemplate(ScenarioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioComponent);
    component = fixture.componentInstance;

    activatedRoute = TestBed.inject(ActivatedRoute);
    alertService = TestBed.inject(AlertService);
    service = TestBed.inject(ScenarioService);
    routerNavigateSpy = jest.spyOn(component.router, 'navigate');
    userPreferenceService = TestBed.inject(UserPreferenceService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ name: 'test-scenario' }],
          headers,
        }),
      ),
    );
  });

  describe('ngOnInit', () => {
    it('should call load all on init with default values', () => {
      // Mock the default return value behaviour
      (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(ITEMS_PER_PAGE);
      (userPreferenceService.getSortState as unknown as SpyInstance).mockReturnValueOnce(
        sortStateSignal({ predicate: 'id', order: SortOrder.ASCENDING }),
      );

      // Mock the activated route accordingly, note the absence of page and sort
      // @ts-expect-error: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          size: ITEMS_PER_PAGE,
          'filter[someId.in]': 'eff151df-0e8e-4f24-8c0b-bff1734c0569',
        }),
      );

      // WHEN
      component.ngOnInit();

      // Make sure data was loaded with default values
      expect(service.query).toHaveBeenCalledWith({ page: 0, size: 10, sort: ['id,asc'] });
      expect(component.scenarios?.[0]).toEqual(expect.objectContaining({ name: 'test-scenario' }));

      // And the user preferences have been saved
      expect(userPreferenceService.setSortState).toHaveBeenCalledWith(
        'scenario',
        expect.objectContaining({ predicate: 'id', order: SortOrder.ASCENDING }),
      );
    });

    it('should call load all on init with tracked values', () => {
      // Create items in the user preference service
      const itemsPerPage = 1234;
      (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);
      const predicate = 'some-predicate';
      (userPreferenceService.getSortState as unknown as SpyInstance).mockReturnValueOnce(
        sortStateSignal({ predicate, order: SortOrder.DESCENDING }),
      );

      // Mock the activated route accordingly
      const sort = predicate + ',desc';
      // @ts-expect-error: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          page: '1',
          size: itemsPerPage,
          sort,
        }),
      );

      // WHEN
      component.ngOnInit();

      // This is the initial read
      expect(userPreferenceService.getPageSize).toHaveBeenCalledWith('scenario');
      expect(userPreferenceService.getSortState).toHaveBeenCalledWith('scenario', 'name');

      // Then follows the update of the route
      expect(routerNavigateSpy).toHaveBeenCalledWith(
        ['./'],
        expect.objectContaining({
          queryParams: {
            page: 1,
            size: 1234,
            sort: ['some-predicate,desc'],
          },
        }),
      );

      // Next, the data will be loaded
      expect(service.query).toHaveBeenCalledWith({ page: 0, size: itemsPerPage, sort: [sort] });
      expect(component.scenarios?.[0]).toEqual(expect.objectContaining({ name: 'test-scenario' }));

      // And the user preferences must be saved
      expect(userPreferenceService.setSortState).toHaveBeenCalledWith(
        'scenario',
        expect.objectContaining({ predicate, order: SortOrder.DESCENDING }),
      );

      // And finally make sure the values have been persisted
      expect(component.itemsPerPage).toEqual(itemsPerPage);
      expect(component.sortState().predicate).toEqual(predicate);
      expect(component.sortState().order).toEqual(SortOrder.DESCENDING);
    });

    it('subscribes to form value changes', () => {
      jest.useFakeTimers();
      try {
        const filterFormValueChangesSubject = new Subject<{
          nameContains: string;
        }>();
        jest.spyOn(filterFormValueChangesSubject, 'subscribe');
        // @ts-expect-error: Override read-only property for testing
        component.filterForm.valueChanges = filterFormValueChangesSubject;

        // @ts-expect-error: Access private function for testing
        activatedRoute.queryParamMap = EMPTY;

        component.ngOnInit();

        // VERIFY that the subscription has been made

        // @ts-expect-error: Access private member for testing
        expect(component.filterFormValueChanges).not.toBeNull();
        // eslint-disable-next-line @typescript-eslint/no-deprecated
        expect(filterFormValueChangesSubject.subscribe).toHaveBeenCalled();

        expect(component.filterForm.getRawValue()).toEqual({ nameContains: null });

        // TRIGGER subscription and expect reload of data

        const nameContains = 'name filter';
        filterFormValueChangesSubject.next({ nameContains });

        jest.advanceTimersByTime(DEBOUNCE_TIME_MILLIS);

        expect(routerNavigateSpy).toHaveBeenNthCalledWith(2, [], { queryParams: { 'filter[scenarioName.contains]': nameContains } });
      } finally {
        jest.useRealTimers();
      }
    });
  });

  describe('ngOnDestroy', () => {
    it('unsubscribes from form value changes', () => {
      const unsubscribe = jest.fn();
      // @ts-expect-error: Access private member for testing
      component.filterFormValueChanges = {
        unsubscribe,
      };

      component.ngOnDestroy();

      expect(unsubscribe).toHaveBeenCalled();
    });
  });

  describe('load', () => {
    it('should ignore empty name filter', () => {
      // Mock the activated route
      const page = 3;
      const predicate = 'some-predicate';
      const sort = predicate + ',desc';

      // @ts-expect-error: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          page,
          sort,
        }),
      );

      component.load();

      expect(service.query).toHaveBeenCalledWith({ page: page - 1, size: 10, sort: [sort] });
    });

    it('should include name filter if it has a value', () => {
      // Mock the activated route
      const page = 3;
      const predicate = 'some-predicate';
      const sort = predicate + ',desc';

      // @ts-expect-error: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          page,
          sort,
        }),
      );

      const nameContains = 'any name';
      component.filterForm.value.nameContains = nameContains;

      component.load();

      expect(service.query).toHaveBeenCalledWith({ page: page - 1, size: 10, sort: [sort], nameContains });
    });
  });

  describe('trackId', () => {
    it('should forward to scenarioService', () => {
      const entity = { name: 'test-scenario' };
      jest.spyOn(service, 'getScenarioIdentifier');
      const name = component.trackId(0, entity);
      expect(service.getScenarioIdentifier).toHaveBeenCalledWith(entity);
      expect(name).toBe(entity.name);
    });
  });

  describe('resetFilter', () => {
    it('should reset form', () => {
      // Assume filter is dirty
      component.filterForm.markAsDirty();

      jest.spyOn(component.filterForm, 'reset');
      jest.spyOn(component.filterForm, 'markAsPristine');

      // @ts-expect-error: Access protected function for testing
      component.resetFilter();

      expect(component.filterForm.reset).toHaveBeenCalled();
      expect(component.filterForm.markAsPristine).toHaveBeenCalled();
      expect(routerNavigateSpy).toHaveBeenCalled();
    });
  });

  describe('navigateToWithComponentValues', () => {
    test.each([
      { predicate: 'name', ascending: true, expectedSort: 'name,asc' },
      { predicate: 'name', ascending: false, expectedSort: 'name,desc' },
    ])('should calculate the sort attribute for ascending=$ascending', ({ predicate, ascending, expectedSort }) => {
      // @ts-expect-error: Access protected method for testing
      component.navigateToWithComponentValues({ predicate, order: ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING });

      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        ['./'],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sort: [expectedSort],
          }),
        }),
      );
      expect(userPreferenceService.setSortState).toHaveBeenCalledWith('scenario', {
        predicate,
        order: ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING,
      });
    });
  });

  describe('navigateToPage', () => {
    it('should load a page', () => {
      // @ts-expect-error: Access private function for testing
      component.navigateToPage(1);

      expect(routerNavigateSpy).toHaveBeenCalled();
    });
  });

  describe('pageSizeChanged', () => {
    it('should adapt to page size changes', () => {
      // Mock the activated route accordingly, note the absence of page and sort
      // @ts-expect-error: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          size: ITEMS_PER_PAGE,
          'filter[someId.in]': 'eff151df-0e8e-4f24-8c0b-bff1734c0569',
        }),
      );

      // New value
      const itemsPerPage = 1234;

      // WHEN
      // @ts-expect-error: Access protected function for testing
      component.pageSizeChanged(itemsPerPage);

      // Make sure new value has been correctly persisted and used
      expect(component.itemsPerPage).toEqual(itemsPerPage);
      expect(service.query).toHaveBeenLastCalledWith({ page: 0, size: itemsPerPage, sort: ['id,asc'] });
    });
  });

  describe('launch', () => {
    const name = 'something I came up with';
    const mockParams: IScenarioParameter[] = [
      {
        parameterId: 1,
        name: 'TEXTBOX',
        controlType: 1,
        value: 'default',
        createdDate: Date.now(),
        lastModifiedDate: null,
      },
    ];

    const mockModalRef = {
      result: Promise.resolve([{ parameterId: 1, name: 'TEXTBOX', value: 'user-input' }]),
      componentInstance: { params: mockParams },
    } as NgbModalRef;

    it('triggers scenario execution in backend', async () => {
      jest.spyOn(service, 'findParameters').mockReturnValue(of(new HttpResponse({ body: mockParams })));

      jest.spyOn(component['modalService'], 'open').mockReturnValue(mockModalRef);

      // Configure mock alert service
      const scenarioId = 1234;
      service.launch = jest.fn().mockReturnValue(of(new HttpResponse({ body: scenarioId })));

      component['launch']({ name });

      // Two flushes: first resolves the modal Promise, second delivers the value into the switchMap
      await Promise.resolve();
      await Promise.resolve();

      expect(service.launch).toHaveBeenCalledWith(name, [{ parameterId: 1, name: 'TEXTBOX', value: 'user-input' }]);
      expect(alertService.addAlert).toHaveBeenCalledWith({
        type: 'success',
        translationKey: 'citrusSimulatorApp.scenario.action.launchedSuccessfully',
        translationParams: { scenarioExecutionId: scenarioId },
      });
    });

    it('handles failures', async () => {
      jest.spyOn(service, 'findParameters').mockReturnValue(of(new HttpResponse({ body: mockParams })));

      jest.spyOn(component['modalService'], 'open').mockReturnValue(mockModalRef);

      // Configure mock alert service
      service.launch = jest.fn().mockReturnValue(throwError(() => new Error('Anything that happen during communication!')));

      // @ts-expect-error: Access private function for testing
      component.launch({ name });
      await Promise.resolve();
      await Promise.resolve();

      expect(service.launch).toHaveBeenCalledWith(name, [{ name: 'TEXTBOX', parameterId: 1, value: 'user-input' }]);
      expect(alertService.addAlert).toHaveBeenCalledWith({
        type: 'danger',
        translationKey: 'citrusSimulatorApp.scenario.action.launchFailed',
      });
    });

    it('should not launch if modal is dismissed', async () => {
      jest.spyOn(service, 'findParameters').mockReturnValue(of(new HttpResponse({ body: mockParams })));

      // Mock modal dismissal
      const mockModalRefRejection = {
        // eslint-disable-next-line @typescript-eslint/prefer-promise-reject-errors
        result: Promise.reject('dismissed'),
        componentInstance: { params: mockParams },
      } as NgbModalRef;

      jest.spyOn(component['modalService'], 'open').mockReturnValue(mockModalRefRejection);

      const launchSpy = jest.spyOn(service, 'launch');

      component['launch']({ name });
      await Promise.resolve();
      await Promise.resolve();

      expect(launchSpy).not.toHaveBeenCalled();
      expect(alertService.addAlert).not.toHaveBeenCalled();
    });
  });
});
