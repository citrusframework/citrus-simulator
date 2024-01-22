import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';

import { UserPreferenceService } from 'app/core/config/user-preference.service';
import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';
import SharedModule from 'app/shared/shared.module';

import ScenarioExecutionFilterComponent from './scenario-execution-filter.component';

@Component({
  standalone: true,
  selector: 'app-scenario-result',
  templateUrl: './scenario-result.component.html',
  imports: [SharedModule, ScenarioExecutionComponent, ScenarioExecutionFilterComponent],
})
export default class ScenarioResultComponent implements AfterViewInit {
  @ViewChild(ScenarioExecutionComponent)
  scenarioExecutionComponent: ScenarioExecutionComponent | null = null;

  itemsPerPageKey = 'scenario-result';

  constructor(private userPreferenceService: UserPreferenceService) {}

  ngAfterViewInit(): void {
    this.pageSizeChanged(this.userPreferenceService.getPageSize(this.itemsPerPageKey));
  }

  pageSizeChanged(pageSize: number): void {
    if (this.scenarioExecutionComponent) {
      this.scenarioExecutionComponent.itemsPerPage = pageSize;
      this.scenarioExecutionComponent.load();
    }
  }
}
