import { Component } from '@angular/core';

import { ScenarioExecutionComponent } from 'app/entities/scenario-execution/list/scenario-execution.component';
import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-scenario-result',
  templateUrl: './scenario-result.component.html',
  imports: [SharedModule, ScenarioExecutionComponent],
})
export default class ScenarioResultComponent {}
