import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { MessageHeaderService } from '../service/message-header.service';

import { MessageHeaderComponent } from './message-header.component';
import SpyInstance = jest.SpyInstance;

describe('MessageHeader Management Component', () => {
  let service: MessageHeaderService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  let fixture: ComponentFixture<MessageHeaderComponent>;
  let component: MessageHeaderComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'message-header', component: MessageHeaderComponent }]),
        provideHttpClientTesting(),
        MessageHeaderComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'headerId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'headerId,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(MessageHeaderComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageHeaderComponent);
    component = fixture.componentInstance;

    service = TestBed.inject(MessageHeaderService);
    routerNavigateSpy = jest.spyOn(component.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ headerId: 123 }],
          headers,
        }),
      ),
    );
  });

  it('should call load all on init', () => {
    // WHEN
    component.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(component.messageHeaders?.[0]).toEqual(expect.objectContaining({ headerId: 123 }));
  });

  it('should load a page', () => {
    // WHEN
    component.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for a headerId', () => {
    // WHEN
    component.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['headerId,desc'] }));
  });

  describe('navigateToWithComponentValues', () => {
    it('should calculate the sort attribute for a non-id attribute', () => {
      // WHEN
      component.navigateToWithComponentValues({ ascending: true, predicate: 'name' });

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

    it('should respect given ascending and predicated', () => {
      // WHEN
      component.navigateToWithComponentValues({ ascending: false, predicate: 'messageHeaderId' });

      // THEN
      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        expect.anything(),
        expect.objectContaining({
          queryParams: expect.objectContaining({
            sort: ['messageHeaderId,desc'],
          }),
        }),
      );
    });
  });

  it('should calculate the filter attribute', () => {
    // WHEN
    component.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });
});
