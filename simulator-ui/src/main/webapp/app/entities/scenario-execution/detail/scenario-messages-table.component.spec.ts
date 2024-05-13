import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import * as operators from 'app/core/util/operators';

import { IMessage } from 'app/entities/message/message.model';

import { ScenarioMessagesTableComponent } from './scenario-messages-table.component';

import SpyInstance = jest.SpyInstance;

describe('Message Table Component', () => {
  let sortSpy: SpyInstance;

  let fixture: ComponentFixture<ScenarioMessagesTableComponent>;
  let component: ScenarioMessagesTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'message', component: ScenarioMessagesTableComponent }]),
        HttpClientTestingModule,
        ScenarioMessagesTableComponent,
      ],
      providers: [],
    })
      .overrideTemplate(ScenarioMessagesTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioMessagesTableComponent);
    component = fixture.componentInstance;

    sortSpy = jest.spyOn(operators, 'sort');
    sortSpy.mockClear();
  });

  describe('ngOnInit', () => {
    it('sorts messages', () => {
      expectSortBeingCalled(() => component.ngOnInit());
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
    expectSortBeingCalled(() => component.sortMessages());
  });

  const expectSortBeingCalled = (whenFunction: () => void): void => {
    const messages = [{ messageId: 1234 }] as IMessage[];
    component.sortedMessages = messages;

    whenFunction();

    expect(sortSpy).toHaveBeenCalledWith(messages, 'messageId', true);
  };
});
