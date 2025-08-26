import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IScenarioParameter } from '../scenario-parameter.model';

@Component({
  selector: 'app-scenario-parameter-detail',
  templateUrl: './scenario-parameter-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ScenarioParameterDetailComponent {
  scenarioParameter = input<IScenarioParameter | null>(null);

  previousState(): void {
    window.history.back();
  }
}
