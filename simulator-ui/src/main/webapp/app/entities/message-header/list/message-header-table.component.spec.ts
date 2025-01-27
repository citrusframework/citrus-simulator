import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import * as operators from 'app/core/util/operators';

import { IMessageHeader } from '../message-header.model';
import { MessageHeaderService } from '../service/message-header.service';

import MessageHeaderTableComponent from './message-header-table.component';
import { provideRouter } from '@angular/router';
import SpyInstance = jest.SpyInstance;

describe('MessageHeader Table Component', () => {
  let sortSpy: SpyInstance;

  let service: MessageHeaderService;

  let fixture: ComponentFixture<MessageHeaderTableComponent>;
  let component: MessageHeaderTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'message-header', component: MessageHeaderTableComponent }]),
        provideHttpClientTesting(),
        MessageHeaderTableComponent,
      ],
      providers: [],
    })
      .overrideTemplate(MessageHeaderTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageHeaderTableComponent);
    component = fixture.componentInstance;

    sortSpy = jest.spyOn(operators, 'sort');
    sortSpy.mockClear();

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

  describe('set messageHeaders', () => {
    it('sets the message header list and calls sort in standalone mode', () => {
      component.standalone = true;

      const messageHeaders = [{ headerId: 1234 }] as IMessageHeader[];

      component.messageHeaders = messageHeaders;

      expect(component.sortedMessageHeaders).toEqual(messageHeaders);
      expect(sortSpy).toHaveBeenCalledWith(messageHeaders, 'headerId', true);
    });

    it('sets the message header list only in non-standalone mode', () => {
      component.standalone = false;

      const messageHeaders = [{ headerId: 1234 }] as IMessageHeader[];

      component.messageHeaders = messageHeaders;

      expect(component.sortedMessageHeaders).toEqual(messageHeaders);
      expect(sortSpy).not.toHaveBeenCalled();
    });
  });

  describe('emitSortChange', () => {
    let sortChange: SpyInstance;

    beforeEach(() => {
      sortChange = jest.spyOn(component.sortChange, 'emit');
    });

    it('sorts in place in standalone mode', () => {
      component.standalone = true;

      const messageHeaders = [{ headerId: 1234 }] as IMessageHeader[];
      component.messageHeaders = messageHeaders;

      // @ts-expect-error: Access protected function for testing
      component.emitSortChange();

      expect(sortSpy).toHaveBeenCalledWith(messageHeaders, 'headerId', true);
      expect(sortChange).not.toHaveBeenCalled();
    });

    it('emits event in non-standalone mode', () => {
      component.standalone = false;

      // @ts-expect-error: Access protected function for testing
      component.emitSortChange();

      expect(sortSpy).not.toHaveBeenCalled();
      expect(sortChange).toHaveBeenCalledWith({ predicate: 'headerId', ascending: true });
    });
  });
});
