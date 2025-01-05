import { AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';

import { EntityOrder } from 'app/config/navigation.constants';

import { UserPreferenceService } from 'app/core/config/user-preference.service';

import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';

import SharedModule from 'app/shared/shared.module';

import ScenarioExecutionFilterComponent from './filter/scenario-execution-filter.component';

@Component({
  standalone: true,
  selector: 'app-scenario-result',
  templateUrl: './scenario-result.component.html',
  imports: [SharedModule, ScenarioExecutionComponent, ScenarioExecutionFilterComponent],
})
export default class ScenarioResultComponent implements AfterViewInit {
  @ViewChild(ScenarioExecutionComponent)
  scenarioExecutionComponent: ScenarioExecutionComponent | null = null;

  @ViewChild(ScenarioExecutionFilterComponent)
  scenarioExecutionFilterComponent: ScenarioExecutionFilterComponent | null = null;

  protected readonly USER_PREFERENCES_KEY = 'scenario-result';

  constructor(
    private userPreferenceService: UserPreferenceService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  ngAfterViewInit(): void {
    if (this.scenarioExecutionComponent) {
      this.scenarioExecutionComponent.itemsPerPage = this.userPreferenceService.getPageSize(this.USER_PREFERENCES_KEY);
      this.scenarioExecutionComponent.predicate = this.userPreferenceService.getPredicate(
        this.USER_PREFERENCES_KEY,
        this.scenarioExecutionComponent.predicate,
      );
      this.scenarioExecutionComponent.ascending =
        this.userPreferenceService.getEntityOrder(this.USER_PREFERENCES_KEY) === EntityOrder.ASCENDING;

      this.scenarioExecutionComponent.navigateToWithComponentValues();
    }

    this.changeDetector.detectChanges();
  }

  protected pageSizeChanged(pageSize: number): void {
    if (this.scenarioExecutionComponent) {
      this.scenarioExecutionComponent.itemsPerPage = pageSize;
      this.scenarioExecutionComponent.navigateToWithComponentValues();
    }
  }

  protected updateUserPreferences({ predicate, ascending }: { predicate: string; ascending: boolean }): void {
    this.userPreferenceService.setPredicate(this.USER_PREFERENCES_KEY, predicate);
    this.userPreferenceService.setEntityOrder(this.USER_PREFERENCES_KEY, ascending ? EntityOrder.ASCENDING : EntityOrder.DESCENDING);
  }

  protected resetFilter(): void {
    if (this.scenarioExecutionFilterComponent) {
      this.scenarioExecutionFilterComponent.resetFilter();
    }
  }
}
