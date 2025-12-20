import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IScenarioAction } from '../scenario-action.model';

@Component({
  standalone: true,
  selector: 'app-scenario-action-detail',
  templateUrl: './scenario-action-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ScenarioActionDetailComponent {
  @Input() scenarioAction: IScenarioAction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
