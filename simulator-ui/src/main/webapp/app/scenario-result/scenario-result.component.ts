import { AfterViewInit, Component, inject, ViewChild } from '@angular/core';

import { UserPreferenceService } from 'app/core/config/user-preference.service';

import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';

import SharedModule from 'app/shared/shared.module';

import ScenarioExecutionFilterComponent from './filter/scenario-execution-filter.component';
import { SelectPageSize } from 'app/shared/pagination';
import { SortState } from 'app/shared/sort';

const USER_PREFERENCES_KEY = 'scenario-result';

@Component({
  standalone: true,
  selector: 'app-scenario-result',
  templateUrl: './scenario-result.component.html',
  imports: [SharedModule, ScenarioExecutionComponent, ScenarioExecutionFilterComponent, SelectPageSize],
})
export default class ScenarioResultComponent implements AfterViewInit {
  userPreferencesKey = USER_PREFERENCES_KEY;

  @ViewChild(ScenarioExecutionComponent)
  scenarioExecutionComponent: ScenarioExecutionComponent | null = null;

  @ViewChild(ScenarioExecutionFilterComponent)
  scenarioExecutionFilterComponent: ScenarioExecutionFilterComponent | null = null;

  private userPreferenceService = inject(UserPreferenceService);

  ngAfterViewInit(): void {
    if (this.scenarioExecutionComponent) {
      const sortState = this.userPreferenceService.getSortState(
        USER_PREFERENCES_KEY,
        this.scenarioExecutionComponent.sortState().predicate!,
      )();
      this.scenarioExecutionComponent.itemsPerPage = this.userPreferenceService.getPageSize(USER_PREFERENCES_KEY);
      this.scenarioExecutionComponent.sortState.set(sortState);
      this.scenarioExecutionComponent.navigateToWithComponentValues();
    }
  }

  protected pageSizeChanged(pageSize: number): void {
    if (this.scenarioExecutionComponent) {
      this.scenarioExecutionComponent.itemsPerPage = pageSize;
      this.scenarioExecutionComponent.navigateToWithComponentValues();
    }
  }

  protected updateUserPreferences(event: SortState): void {
    this.userPreferenceService.setSortState(USER_PREFERENCES_KEY, event);
  }

  protected resetFilter(): void {
    if (this.scenarioExecutionFilterComponent) {
      this.scenarioExecutionFilterComponent.resetFilter();
    }
  }
}
