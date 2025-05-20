import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ScenarioParameterService } from '../service/scenario-parameter.service';

import { ScenarioParameterComponent } from './scenario-parameter.component';
import SpyInstance = jest.SpyInstance;

describe('ScenarioParameter Management Component', () => {
  let comp: ScenarioParameterComponent;
  let fixture: ComponentFixture<ScenarioParameterComponent>;
  let service: ScenarioParameterService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'scenario-parameter', component: ScenarioParameterComponent }]),
        provideHttpClientTesting(),
        ScenarioParameterComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'parameterId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'parameterId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ScenarioParameterComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioParameterComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScenarioParameterService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ parameterId: 123 }],
          headers,
        }),
      ),
    );
  });

  it('should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.scenarioParameters?.[0]).toEqual(expect.objectContaining({ parameterId: 123 }));
  });

  describe('trackId', () => {
    it('should forward to scenarioParameterService', () => {
      const entity = { parameterId: 123 };
      jest.spyOn(service, 'getScenarioParameterIdentifier');
      const parameterId = comp.trackId(0, entity);
      expect(service.getScenarioParameterIdentifier).toHaveBeenCalledWith(entity);
      expect(parameterId).toBe(entity.parameterId);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for a parameterId', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['parameterId,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.navigateToWithComponentValues();

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      }),
    );
  });

  it('should calculate the filter attribute', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });
});
