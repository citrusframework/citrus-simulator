import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IScenarioAction } from '../scenario-action.model';

@Component({
  selector: 'app-scenario-action-detail',
  templateUrl: './scenario-action-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ScenarioActionDetailComponent {
  scenarioAction = input<IScenarioAction | null>(null);

  previousState(): void {
    window.history.back();
  }
}
