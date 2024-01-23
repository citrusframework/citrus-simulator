import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';

import ScenarioResultComponent from './scenario-result.component';
import { EntityOrder } from '../config/navigation.constants';
import { ChangeDetectorRef } from '@angular/core';

import SpyInstance = jest.SpyInstance;

const itemsPerPage = 1234;

describe('ScenarioResult Component', () => {
  let scenarioExecutionComponent: ScenarioExecutionComponent;
  let userPreferenceService: UserPreferenceService;

  let fixture: ComponentFixture<ScenarioResultComponent>;
  let component: ScenarioResultComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ScenarioResultComponent],
      providers: [
        {
          provide: UserPreferenceService,
          useValue: {
            getPageSize: jest.fn(),
            getPredicate: jest.fn(),
            setPredicate: jest.fn(),
            getEntityOrder: jest.fn(),
            setEntityOrder: jest.fn(),
          },
        },
      ],
    })
      .overrideTemplate(ScenarioResultComponent, '')
      .compileComponents();

    scenarioExecutionComponent = {
      itemsPerPage: 0,
      navigateToWithComponentValues: jest.fn(),
      load: jest.fn(),
    } as unknown as ScenarioExecutionComponent;

    userPreferenceService = TestBed.inject(UserPreferenceService);

    fixture = TestBed.createComponent(ScenarioResultComponent);
    component = fixture.componentInstance;
  });

  describe('ngAfterViewInit', () => {
    let detectChangesSpy: SpyInstance<any>;

    beforeEach(() => {
      const changeDetectorRef = fixture.debugElement.injector.get(ChangeDetectorRef);
      detectChangesSpy = jest.spyOn(changeDetectorRef.constructor.prototype, 'detectChanges');
    });

    test.each([{ entityOrder: EntityOrder.ASCENDING }, { entityOrder: EntityOrder.DESCENDING }])(
      'initially loads page size',
      ({ entityOrder }) => {
        (userPreferenceService.getPageSize as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);
        (userPreferenceService.getPredicate as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);
        (userPreferenceService.getEntityOrder as unknown as SpyInstance).mockReturnValueOnce(entityOrder);
        component.scenarioExecutionComponent = scenarioExecutionComponent;

        component.ngAfterViewInit();

        expect(scenarioExecutionComponent.itemsPerPage).toEqual(itemsPerPage);
        expect(scenarioExecutionComponent.predicate).toEqual(itemsPerPage);
        expect(scenarioExecutionComponent.ascending).toEqual(entityOrder === EntityOrder.ASCENDING);

        expect(scenarioExecutionComponent.navigateToWithComponentValues).toHaveBeenCalled();

        expect(detectChangesSpy).toHaveBeenCalled();
      },
    );
  });

  describe('pageSizeChanged', () => {
    it('reloads the component if it exists', () => {
      component.scenarioExecutionComponent = scenarioExecutionComponent;

      // @ts-ignore: Access private function for testing
      component.pageSizeChanged(itemsPerPage);

      expect(scenarioExecutionComponent.itemsPerPage).toEqual(itemsPerPage);
      expect(scenarioExecutionComponent.load).toHaveBeenCalled();
    });

    it('does nothing if component does not exist', () => {
      // @ts-ignore: Access private function for testing
      component.pageSizeChanged(itemsPerPage);
      expect(scenarioExecutionComponent.itemsPerPage).toEqual(0);
      expect(scenarioExecutionComponent.load).not.toHaveBeenCalled();
    });
  });

  describe('updateUserPreferences', () => {
    test.each([
      { predicate: 'predicate', ascending: true, expectedEntityOrder: EntityOrder.ASCENDING },
      { predicate: 'predicate', ascending: false, expectedEntityOrder: EntityOrder.DESCENDING },
    ])('persists the values into the user service', ({ predicate, ascending, expectedEntityOrder }) => {
      // @ts-ignore: Access private function for testing
      component.updateUserPreferences({ predicate, ascending });

      expect(userPreferenceService.setPredicate).toHaveBeenCalledWith('scenario-result', predicate);
      expect(userPreferenceService.setEntityOrder).toHaveBeenCalledWith('scenario-result', expectedEntityOrder);
    });
  });
});
