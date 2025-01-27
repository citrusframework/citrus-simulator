import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import * as operators from 'app/core/util/operators';

import { IScenarioAction } from 'app/entities/scenario-action/scenario-action.model';

import { ScenarioActionsTableComponent } from './scenario-actions-table.component';
import { provideRouter } from '@angular/router';
import SpyInstance = jest.SpyInstance;

describe('Message Table Component', () => {
  let sortSpy: SpyInstance;

  let fixture: ComponentFixture<ScenarioActionsTableComponent>;
  let component: ScenarioActionsTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'message', component: ScenarioActionsTableComponent }]),
        provideHttpClientTesting(),
        ScenarioActionsTableComponent,
      ],
      providers: [],
    })
      .overrideTemplate(ScenarioActionsTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioActionsTableComponent);
    component = fixture.componentInstance;

    sortSpy = jest.spyOn(operators, 'sort');
    sortSpy.mockClear();
  });

  describe('ngOnInit', () => {
    it('sorts actions', () => {
      expectSortBeingCalled(() => component.ngOnInit());
    });
  });

  describe('set actions', () => {
    it('sets the action list and calls sort', () => {
      const actions = [{ actionId: 1234 }] as IScenarioAction[];

      component.actions = actions;

      expect(component.sortedActions).toEqual(actions);
      expect(sortSpy).toHaveBeenCalledWith(actions, 'actionId', true);
    });
  });

  it('sorts actions', () => {
    expectSortBeingCalled(() => component.sortActions());
  });

  const expectSortBeingCalled = (whenFunction: () => void): void => {
    const actions = [{ actionId: 1234 }] as IScenarioAction[];
    component.sortedActions = actions;

    whenFunction();

    expect(sortSpy).toHaveBeenCalledWith(actions, 'actionId', true);
  };
});
