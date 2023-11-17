import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

import { UserPreferenceService } from 'app/core/config/user-preference.service';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';

import SpyInstance = jest.SpyInstance;

describe('Scenario Management Component', () => {
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
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
        {
          provide: UserPreferenceService,
          useValue: {
            getPreferredPageSize: jest.fn(),
          },
        },
      ],
    })
      .overrideTemplate(ScenarioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioComponent);
    component = fixture.componentInstance;

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

  it('should call load all on init', () => {
    const itemsPerPage = 1234;
    (userPreferenceService.getPreferredPageSize as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);

    component.ngOnInit();

    expect(component.itemsPerPage).toEqual(itemsPerPage);
    expect(service.query).toHaveBeenCalledWith({ page: 0, size: itemsPerPage, sort: ['id,desc'] });
    expect(component.scenarios?.[0]).toEqual(expect.objectContaining({ name: 'test-scenario' }));
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

  it('should calculate the sort attribute for an id', () => {
    component.ngOnInit();

    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['id,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    component.predicate = 'name';

    component.navigateToWithComponentValues();

    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      }),
    );
  });

  it('should adapt to page size changes', () => {
    const itemsPerPage = 1234;

    // @ts-ignore: Access private function for testing
    component.pageSizeChanged(itemsPerPage);

    expect(component.itemsPerPage).toEqual(itemsPerPage);
    expect(service.query).toHaveBeenLastCalledWith({ page: 0, size: itemsPerPage, sort: ['id,desc'] });
  });
});
