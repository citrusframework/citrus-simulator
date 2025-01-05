import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';

import { ScenarioActionsTableComponent } from './scenario-actions-table.component';
import { ScenarioMessagesTableComponent } from './scenario-messages-table.component';
import { ScenarioParametersTableComponent } from './scenario-parameters-table.component';

import { IScenarioExecution } from '../scenario-execution.model';

@Component({
  standalone: true,
  selector: 'app-scenario-execution-detail',
  templateUrl: './scenario-execution-detail.component.html',
  imports: [
    SharedModule,
    RouterModule,
    FormatMediumDatetimePipe,
    ScenarioActionsTableComponent,
    ScenarioMessagesTableComponent,
    ScenarioParametersTableComponent,
  ],
})
export class ScenarioExecutionDetailComponent {
  @Input() scenarioExecution: IScenarioExecution | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
