import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import * as operators from 'app/core/util/operators';

import { MessageService } from 'app/entities/message/service/message.service';

import { MessageTableComponent } from './message-table.component';

import SpyInstance = jest.SpyInstance;
import { IMessageHeader } from '../../message-header/message-header.model';
import { IMessage } from '../../message/message.model';

describe('Message Table Component', () => {
  let sortSpy: SpyInstance;

  let service: MessageService;

  let fixture: ComponentFixture<MessageTableComponent>;
  let component: MessageTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'message', component: MessageTableComponent }]),
        HttpClientTestingModule,
        MessageTableComponent,
      ],
      providers: [],
    })
      .overrideTemplate(MessageTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageTableComponent);
    component = fixture.componentInstance;

    sortSpy = jest.spyOn(operators, 'sort');
    sortSpy.mockClear();
  });

  describe('ngOnInit', () => {
    it('sorts messages', () => {
      extracted(() => component.ngOnInit());
    });
  });

  describe('set messages', () => {
    it('sets the message list and calls sort', () => {
      const messages = [{ messageId: 1234 }] as IMessage[];

      component.messages = messages;

      expect(component.sortedMessages).toEqual(messages);
      expect(sortSpy).toHaveBeenCalledWith(messages, 'messageId', true);
    });
  });

  it('sorts messages', () => {
    extracted(() => component.sortMessages());
  });

  const extracted = (whenFunction: () => void): void => {
    const messages = [{ messageId: 1234 }] as IMessage[];
    component.sortedMessages = messages;

    whenFunction();

    expect(sortSpy).toHaveBeenCalledWith(messages, 'messageId', true);
  };
});
