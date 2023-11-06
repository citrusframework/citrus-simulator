import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IScenarioParameter } from '../scenario-parameter.model';

@Component({
  standalone: true,
  selector: 'app-scenario-parameter-detail',
  templateUrl: './scenario-parameter-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ScenarioParameterDetailComponent {
  @Input() scenarioParameter: IScenarioParameter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
