import { Component, ViewChild } from '@angular/core';

import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';
import SharedModule from 'app/shared/shared.module';

import ScenarioExecutionFilterComponent from './scenario-execution-filter.component';

@Component({
  standalone: true,
  selector: 'app-scenario-result',
  templateUrl: './scenario-result.component.html',
  imports: [SharedModule, ScenarioExecutionComponent, ScenarioExecutionFilterComponent],
})
export default class ScenarioResultComponent {
  @ViewChild(ScenarioExecutionComponent)
  scenarioExecutionComponent: ScenarioExecutionComponent | null = null;
}
