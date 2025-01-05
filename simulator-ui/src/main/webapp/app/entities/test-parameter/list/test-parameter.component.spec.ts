import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { TestParameterService } from '../service/test-parameter.service';

import { TestParameterComponent } from './test-parameter.component';
import SpyInstance = jest.SpyInstance;

describe('TestParameter Management Component', () => {
  let comp: TestParameterComponent;
  let fixture: ComponentFixture<TestParameterComponent>;
  let service: TestParameterService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'test-parameter', component: TestParameterComponent }]),
        provideHttpClientTesting(),
        TestParameterComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'createdDate,desc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'createdDate,desc',
                'filter[someKey.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(TestParameterComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestParameterComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TestParameterService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ key: 'key', testResult: { id: 123 } }],
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
    expect(comp.testParameters?.[0]).toEqual(expect.objectContaining({ key: 'key', testResult: { id: 123 } }));
  });

  describe('trackId', () => {
    it('should forward to testParameterService', () => {
      const entity = { key: 'key', testResult: { id: 123 } };
      jest.spyOn(service, 'getTestParameterIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTestParameterIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(2018011204); // This is a hash of the composite primary key
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['createdDate,desc'] }));
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someKey.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });
});
