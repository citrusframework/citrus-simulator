import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
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
      imports: [ScenarioActionComponent],
      providers: [
        provideHttpClient(),
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
            snapshot: {
              queryParams: {},
              queryParamMap: jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'actionId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
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

    jest
      .spyOn(service, 'query')
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ actionId: 2674 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=1&size=20>; rel="next"',
            }),
          }),
        ),
      )
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ actionId: 8050 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=0&size=20>; rel="prev",<http://localhost/api/foo?page=2&size=20>; rel="next"',
            }),
          }),
        ),
      );
  });

  it('should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.scenarioActions()[0]).toEqual(expect.objectContaining({ actionId: 2674 }));
  });

  describe('trackId', () => {
    it('should forward to scenarioActionService', () => {
      const entity = { actionId: 2674 };
      jest.spyOn(service, 'getScenarioActionIdentifier');
      const actionId = comp.trackId(entity);
      expect(service.getScenarioActionIdentifier).toHaveBeenCalledWith(entity);
      expect(actionId).toBe(entity.actionId);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
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

  it('should calculate the filter attribute', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });
});
