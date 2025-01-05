import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ScenarioActionService } from '../service/scenario-action.service';

import { ScenarioActionComponent } from './scenario-action.component';
import SpyInstance = jest.SpyInstance;

describe('ScenarioAction Management Component', () => {
  let comp: ScenarioActionComponent;
  let fixture: ComponentFixture<ScenarioActionComponent>;
  let service: ScenarioActionService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'scenario-action', component: ScenarioActionComponent }]),
        provideHttpClientTesting(),
        ScenarioActionComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'actionId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'actionId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ScenarioActionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioActionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScenarioActionService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ actionId: 123 }],
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
    expect(comp.scenarioActions?.[0]).toEqual(expect.objectContaining({ actionId: 123 }));
  });

  describe('trackId', () => {
    it('should forward to scenarioActionService', () => {
      const entity = { actionId: 123 };
      jest.spyOn(service, 'getScenarioActionIdentifier');
      const actionId = comp.trackId(0, entity);
      expect(service.getScenarioActionIdentifier).toHaveBeenCalledWith(entity);
      expect(actionId).toBe(entity.actionId);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['actionId,desc'] }));
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
