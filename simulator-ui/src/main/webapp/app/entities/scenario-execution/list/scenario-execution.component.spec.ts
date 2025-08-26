import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ScenarioExecutionService } from '../service/scenario-execution.service';

import { ScenarioExecutionComponent } from './scenario-execution.component';
import SpyInstance = jest.SpyInstance;

describe('ScenarioExecution Management Component', () => {
  let comp: ScenarioExecutionComponent;
  let fixture: ComponentFixture<ScenarioExecutionComponent>;
  let service: ScenarioExecutionService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScenarioExecutionComponent],
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'executionId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'executionId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'executionId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
          },
        },
      ],
    })
      .overrideTemplate(ScenarioExecutionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioExecutionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScenarioExecutionService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    jest
      .spyOn(service, 'query')
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ executionId: 7089 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=1&size=20>; rel="next"',
            }),
          }),
        ),
      )
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ executionId: 8486 }],
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
    expect(comp.scenarioExecutions()[0]).toEqual(expect.objectContaining({ executionId: 7089 }));
  });

  describe('trackId', () => {
    it('should forward to scenarioExecutionService', () => {
      const entity = { executionId: 7089 };
      jest.spyOn(service, 'getScenarioExecutionIdentifier');
      const executionId = comp.trackId(entity);
      expect(service.getScenarioExecutionIdentifier).toHaveBeenCalledWith(entity);
      expect(executionId).toBe(entity.executionId);
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['executionId,desc'] }));
  });

  it('should calculate the filter attribute', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });
});
