import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IScenarioExecution } from '../scenario-execution.model';

@Component({
  standalone: true,
  selector: 'app-scenario-execution-detail',
  templateUrl: './scenario-execution-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ScenarioExecutionDetailComponent {
  @Input() scenarioExecution: IScenarioExecution | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
