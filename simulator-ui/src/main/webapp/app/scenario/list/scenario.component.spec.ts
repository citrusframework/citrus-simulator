jest.mock('app/core/util/alert.service');

import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

import { DESC, EntityOrder } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { AlertService } from 'app/core/util/alert.service';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';

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
        RouterTestingModule.withRoutes([{ path: 'scenario', component: ScenarioComponent }]),
        HttpClientTestingModule,
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
    let detectChangesSpy: SpyInstance<any>;

    beforeEach(() => {
      const changeDetectorRef = fixture.debugElement.injector.get(ChangeDetectorRef);
      detectChangesSpy = jest.spyOn(changeDetectorRef.constructor.prototype, 'detectChanges');
    });

    it('should call load all on init with default values', () => {
      // Since the page size has no default in the route, we must mock a value
      (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(ITEMS_PER_PAGE);

      // Mock the activated route accordingly, note the absence of page and sort
      // @ts-ignore: Access private function for testing
      (activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          size: ITEMS_PER_PAGE,
          'filter[someId.in]': 'eff151df-0e8e-4f24-8c0b-bff1734c0569',
        }),
      )),
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
      // @ts-ignore: Access private function for testing
      (activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          page: '1',
          size: itemsPerPage,
          sort,
        }),
      )),
        // WHEN
        component.ngOnInit();

      // This is the initial read
      expect(userPreferenceService.getPageSize).toHaveBeenCalledWith('scenario');
      expect(userPreferenceService.getPredicate).toHaveBeenCalledWith('scenario', 'id');
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
      expect(component.entityOrder).toEqual(DESC);
      expect(component.ascending).toBeFalsy();
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

  it('should load a page', () => {
    component.navigateToPage(1);

    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  describe('navigateToWithComponentValues', () => {
    it('should calculate the sort attribute for a non-id attribute', () => {
      component.predicate = 'name';

      component.navigateToWithComponentValues();

      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        ['./'],
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sort: ['name,asc'],
          }),
        }),
      );
    });
  });

  describe('pageSizeChanged', () => {
    it('should adapt to page size changes', () => {
      // Mock the activated route accordingly, note the absence of page and sort
      // @ts-ignore: Access private function for testing
      activatedRoute.queryParamMap = of(
        jest.requireActual('@angular/router').convertToParamMap({
          size: ITEMS_PER_PAGE,
          'filter[someId.in]': 'eff151df-0e8e-4f24-8c0b-bff1734c0569',
        }),
      );

      // New value
      const itemsPerPage = 1234;

      // WHEN
      // @ts-ignore: Access private function for testing
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

      // @ts-ignore: Access private function for testing
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

      // @ts-ignore: Access private function for testing
      component.launch({ name });

      expect(service.launch).toHaveBeenCalledWith(name);
      expect(alertService.addAlert).toHaveBeenCalledWith({
        type: 'danger',
        translationKey: 'citrusSimulatorApp.scenario.action.launchFailed',
      });
    });
  });
});
