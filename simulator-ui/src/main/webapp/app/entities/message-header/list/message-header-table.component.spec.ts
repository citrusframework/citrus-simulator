import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MessageHeaderService } from '../service/message-header.service';

import MessageHeaderTableComponent from './message-header-table.component';

describe('MessageHeader Table Component', () => {
  let service: MessageHeaderService;

  let fixture: ComponentFixture<MessageHeaderTableComponent>;
  let component: MessageHeaderTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'message-header', component: MessageHeaderTableComponent }]),
        HttpClientTestingModule,
        MessageHeaderTableComponent,
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
      .overrideTemplate(MessageHeaderTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageHeaderTableComponent);
    component = fixture.componentInstance;

    service = TestBed.inject(MessageHeaderService);

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

  describe('trackId', () => {
    it('should forward to messageHeaderService', () => {
      const entity = { headerId: 123 };
      jest.spyOn(service, 'getMessageHeaderIdentifier');
      const headerId = component.trackId(0, entity);
      expect(service.getMessageHeaderIdentifier).toHaveBeenCalledWith(entity);
      expect(headerId).toBe(entity.headerId);
    });
  });
});
