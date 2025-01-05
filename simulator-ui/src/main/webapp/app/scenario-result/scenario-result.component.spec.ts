import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';

import { EntityOrder } from '../config/navigation.constants';

import ScenarioResultComponent from './scenario-result.component';

import SpyInstance = jest.SpyInstance;
import ScenarioExecutionFilterComponent from './filter/scenario-execution-filter.component';

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
    let detectChangesSpy: SpyInstance;

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

        const defaultPredicate = 'default-predicate';
        scenarioExecutionComponent.predicate = defaultPredicate;

        component.scenarioExecutionComponent = scenarioExecutionComponent;

        component.ngAfterViewInit();

        expect(userPreferenceService.getPredicate).toHaveBeenCalledWith('scenario-result', defaultPredicate);

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

      // @ts-expect-error: Access private function for testing
      component.pageSizeChanged(itemsPerPage);

      expect(scenarioExecutionComponent.itemsPerPage).toEqual(itemsPerPage);
      expect(scenarioExecutionComponent.navigateToWithComponentValues).toHaveBeenCalled();
    });

    it('does nothing if component does not exist', () => {
      // @ts-expect-error: Access private function for testing
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
      // @ts-expect-error: Access private function for testing
      component.updateUserPreferences({ predicate, ascending });

      expect(userPreferenceService.setPredicate).toHaveBeenCalledWith('scenario-result', predicate);
      expect(userPreferenceService.setEntityOrder).toHaveBeenCalledWith('scenario-result', expectedEntityOrder);
    });
  });

  describe('resetFilter', () => {
    test('resets filter component if present', () => {
      component.scenarioExecutionFilterComponent = {
        resetFilter: jest.fn(),
      } as unknown as ScenarioExecutionFilterComponent;

      // @ts-expect-error: Access protected function for testing
      component.resetFilter();

      expect(component.scenarioExecutionFilterComponent.resetFilter).toHaveBeenCalled();
    });
  });
});
