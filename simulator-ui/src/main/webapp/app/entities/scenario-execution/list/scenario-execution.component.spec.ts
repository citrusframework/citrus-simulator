import { EventEmitter } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

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
      imports: [
        provideRouter([{ path: 'scenario-execution', component: ScenarioExecutionComponent }]),
        provideHttpClientTesting(),
        ScenarioExecutionComponent,
      ],
      providers: [
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
            snapshot: { queryParams: {} },
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

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ executionId: 123 }],
          headers,
        }),
      ),
    );
  });

  describe('ngOnInit', () => {
    it('should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.scenarioExecutions?.[0]).toEqual(expect.objectContaining({ executionId: 123 }));
    });

    it('should calculate the filter attribute', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['executionId,desc'] }));
    });
  });

  describe('trackId', () => {
    it('should forward to scenarioExecutionService', () => {
      const entity = { executionId: 123 };
      jest.spyOn(service, 'getScenarioExecutionIdentifier');
      const executionId = comp.trackId(0, entity);
      expect(service.getScenarioExecutionIdentifier).toHaveBeenCalledWith(entity);
      expect(executionId).toBe(entity.executionId);
    });
  });

  describe('navigateToPage', () => {
    it('should load a page', () => {
      // WHEN
      comp.navigateToPage(1);

      // THEN
      expect(routerNavigateSpy).toHaveBeenCalled();
    });
  });

  describe('navigateToWithComponentValues', () => {
    it('should calculate the sort attribute for a non-id attribute', () => {
      // GIVEN
      const predicate = 'name';
      comp.predicate = predicate;
      comp.sortChange = { emit: jest.fn() } as unknown as EventEmitter<any>;

      // WHEN
      comp.navigateToWithComponentValues();

      // THEN
      expect(comp.sortChange.emit).toHaveBeenCalledWith({ predicate, ascending: true });
      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        expect.anything(),
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sort: ['name,asc'],
          }),
        }),
      );
    });
  });
});
