import { DEBOUNCE_TIME_MILLIS } from '../../config/input.constants';
import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { EMPTY, of, Subject, throwError } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

import { EntityOrder } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { AlertService } from 'app/core/util/alert.service';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';

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
      imports: [
        provideRouter([{ path: 'scenario', component: ScenarioComponent }]),
        provideHttpClientTesting(),
        ScenarioComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
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
            getPredicate: jest.fn(),
            setPredicate: jest.fn(),
            getEntityOrder: jest.fn(),
            setEntityOrder: jest.fn(),
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
    let detectChangesSpy: SpyInstance;

    beforeEach(() => {
      const changeDetectorRef = fixture.debugElement.injector.get(ChangeDetectorRef);
      detectChangesSpy = jest.spyOn(changeDetectorRef.constructor.prototype, 'detectChanges');
    });

    it('should call load all on init with default values', () => {
      // Mock the default return value behaviour
      (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(ITEMS_PER_PAGE);
      (userPreferenceService.getPredicate as unknown as SpyInstance).mockReturnValueOnce('id');
      (userPreferenceService.getEntityOrder as unknown as SpyInstance).mockReturnValueOnce(EntityOrder.ASCENDING);

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
      expect(userPreferenceService.setPredicate).toHaveBeenCalledWith('scenario', 'id');
      expect(userPreferenceService.setEntityOrder).toHaveBeenCalledWith('scenario', 'asc');

      expect(detectChangesSpy).toHaveBeenCalled();
    });

    it('should call load all on init with tracked values', () => {
      // Create items in the user preference service
      const itemsPerPage = 1234;
      (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);
      const predicate = 'some-predicate';
      (userPreferenceService.getPredicate as unknown as SpyInstance).mockReturnValueOnce(predicate);
      (userPreferenceService.getEntityOrder as unknown as SpyInstance).mockReturnValueOnce(EntityOrder.DESCENDING);

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
      expect(userPreferenceService.getPredicate).toHaveBeenCalledWith('scenario', 'name');
      expect(userPreferenceService.getEntityOrder).toHaveBeenCalledWith('scenario');

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
      expect(userPreferenceService.setPredicate).toHaveBeenCalledWith('scenario', predicate);
      expect(userPreferenceService.setEntityOrder).toHaveBeenCalledWith('scenario', 'desc');

      // Verify that the changes have been registered
      expect(detectChangesSpy).toHaveBeenCalled();

      // And finally make sure the values have been persisted
      expect(component.itemsPerPage).toEqual(itemsPerPage);
      expect(component.predicate).toEqual(predicate);
      expect(component.entityOrder).toEqual(EntityOrder.DESCENDING);
      expect(component.ascending).toBeFalsy();
    });

    it('subscribes to form value changes', fakeAsync(() => {
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

      tick(DEBOUNCE_TIME_MILLIS);

      expect(routerNavigateSpy).toHaveBeenNthCalledWith(2, [], { queryParams: { 'filter[scenarioName.contains]': nameContains } });
    }));
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
      // @ts-expect-error: Access private function for testing
      component.navigateToWithComponentValues({ predicate, ascending });

      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        ['./'],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sort: [expectedSort],
          }),
        }),
      );
      expect(userPreferenceService.setPredicate).toHaveBeenCalledWith('scenario', predicate);
      expect(userPreferenceService.setEntityOrder).toHaveBeenCalledWith(
        'scenario',
        ascending ? EntityOrder.ASCENDING : EntityOrder.DESCENDING,
      );
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
      // @ts-expect-error: Access private function for testing
      component.pageSizeChanged(itemsPerPage);

      // Make sure new value has been correctly persisted and used
      expect(component.itemsPerPage).toEqual(itemsPerPage);
      expect(service.query).toHaveBeenLastCalledWith({ page: 0, size: itemsPerPage, sort: ['id,asc'] });
    });
  });

  describe('launch', () => {
    const name = 'something I came up with';

    it('triggers scenario execution in backend', () => {
      // Configure mock alert service
      const scenarioId = 1234;
      service.launch = jest.fn().mockReturnValue(of(new HttpResponse({ body: scenarioId })));

      // @ts-expect-error: Access private function for testing
      component.launch({ name });

      expect(alertService.addAlert).toHaveBeenCalledWith({
        type: 'success',
        translationKey: 'citrusSimulatorApp.scenario.action.launchedSuccessfully',
        translationParams: {
          scenarioExecutionId: scenarioId,
        },
      });
    });

    it('handles failures', () => {
      // Configure mock alert service
      service.launch = jest.fn().mockReturnValue(throwError(() => new Error('Anything that happen during communication!')));

      // @ts-expect-error: Access private function for testing
      component.launch({ name });

      expect(service.launch).toHaveBeenCalledWith(name);
      expect(alertService.addAlert).toHaveBeenCalledWith({
        type: 'danger',
        translationKey: 'citrusSimulatorApp.scenario.action.launchFailed',
      });
    });
  });
});
