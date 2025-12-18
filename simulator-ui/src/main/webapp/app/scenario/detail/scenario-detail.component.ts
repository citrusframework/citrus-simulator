import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';
import { ScenarioParameterService } from 'app/entities/scenario-parameter/service/scenario-parameter.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-scenario-detail',
  templateUrl: './scenario-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ScenarioDetailComponent {
  @Input() name: string | null = null;
  @Input() type: 'STARTER' | 'MESSAGE_TRIGGERED' | null = null;

  @Input() scenarioParameters: IScenarioParameter[] | null = null;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private scenarioParameterService: ScenarioParameterService,
  ) {}

  trackId = (_index: number, item: IScenarioParameter): number => this.scenarioParameterService.getScenarioParameterIdentifier(item);

  previousState(): void {
    window.history.back();
  }
}
