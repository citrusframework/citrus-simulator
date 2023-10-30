import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TranslateModule } from '@ngx-translate/core';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';

import SpyInstance = jest.SpyInstance;

describe('Scenario Management Component', () => {
  let comp: ScenarioComponent;
  let fixture: ComponentFixture<ScenarioComponent>;
  let service: ScenarioService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

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
      ],
    })
      .overrideTemplate(ScenarioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScenarioService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

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

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.scenarios?.[0]).toEqual(expect.objectContaining({ name: 'test-scenario' }));
  });

  describe('trackId', () => {
    it('Should forward to scenarioService', () => {
      const entity = { name: 'test-scenario' };
      jest.spyOn(service, 'getScenarioIdentifier');
      const name = comp.trackId(0, entity);
      expect(service.getScenarioIdentifier).toHaveBeenCalledWith(entity);
      expect(name).toBe(entity.name);
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['id,desc'] }));
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

  it('should calculate no filter attribute', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith({ page: 0, size: 20, sort: ['id,desc'] });
  });
});
