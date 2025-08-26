import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IScenarioExecution } from '../scenario-execution.model';

@Component({
  selector: 'app-scenario-execution-detail',
  templateUrl: './scenario-execution-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ScenarioExecutionDetailComponent {
  scenarioExecution = input<IScenarioExecution | null>(null);

  previousState(): void {
    window.history.back();
  }
}
