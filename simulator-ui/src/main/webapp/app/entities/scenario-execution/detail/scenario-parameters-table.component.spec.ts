import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import * as operators from 'app/core/util/operators';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';

import { ScenarioParametersTableComponent } from './scenario-parameters-table.component';
import { provideRouter } from '@angular/router';
import SpyInstance = jest.SpyInstance;

describe('Message Table Component', () => {
  let sortSpy: SpyInstance;

  let fixture: ComponentFixture<ScenarioParametersTableComponent>;
  let component: ScenarioParametersTableComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        provideRouter([{ path: 'parameter', component: ScenarioParametersTableComponent }]),
        provideHttpClientTesting(),
        ScenarioParametersTableComponent,
      ],
      providers: [],
    })
      .overrideTemplate(ScenarioParametersTableComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioParametersTableComponent);
    component = fixture.componentInstance;

    sortSpy = jest.spyOn(operators, 'sort');
    sortSpy.mockClear();
  });

  describe('ngOnInit', () => {
    it('sorts parameters', () => {
      expectSortBeingCalled(() => component.ngOnInit());
    });
  });

  describe('set parameters', () => {
    it('sets the parameter list and calls sort', () => {
      const parameters = [{ parameterId: 1234 }] as IScenarioParameter[];

      component.parameters = parameters;

      expect(component.sortedParameters).toEqual(parameters);
      expect(sortSpy).toHaveBeenCalledWith(parameters, 'parameterId', true);
    });
  });

  it('sorts parameters', () => {
    expectSortBeingCalled(() => component.sortParameters());
  });

  const expectSortBeingCalled = (whenFunction: () => void): void => {
    const parameters = [{ parameterId: 1234 }] as IScenarioParameter[];
    component.parameters = parameters;

    whenFunction();

    expect(sortSpy).toHaveBeenCalledWith(parameters, 'parameterId', true);
  };
});
