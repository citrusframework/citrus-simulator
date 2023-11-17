import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';

import ScenarioResultComponent from './scenario-result.component';

type SpyInstance = jest.SpyInstance;

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
            getPreferredPageSize: jest.fn(),
          },
        },
      ],
    })
      .overrideTemplate(ScenarioResultComponent, '')
      .compileComponents();

    scenarioExecutionComponent = {
      itemsPerPage: 0,
      load: jest.fn(),
    } as unknown as ScenarioExecutionComponent;

    userPreferenceService = TestBed.inject(UserPreferenceService);

    fixture = TestBed.createComponent(ScenarioResultComponent);
    component = fixture.componentInstance;
  });

  describe('ngAfterViewInit', () => {
    it('initially loads page size', () => {
      (userPreferenceService.getPreferredPageSize as unknown as SpyInstance).mockReturnValueOnce(itemsPerPage);
      component.scenarioExecutionComponent = scenarioExecutionComponent;

      component.ngAfterViewInit();

      expect(scenarioExecutionComponent.itemsPerPage).toEqual(itemsPerPage);
      expect(scenarioExecutionComponent.load).toHaveBeenCalled();
    });
  });

  describe('pageSizeChanged', () => {
    it('reloads the component if it exists', () => {
      component.scenarioExecutionComponent = scenarioExecutionComponent;

      component.pageSizeChanged(itemsPerPage);

      expect(scenarioExecutionComponent.itemsPerPage).toEqual(itemsPerPage);
      expect(scenarioExecutionComponent.load).toHaveBeenCalled();
    });

    it('does nothing if component does not exist', () => {
      component.pageSizeChanged(itemsPerPage);
      expect(scenarioExecutionComponent.itemsPerPage).toEqual(0);
    });
  });
});
