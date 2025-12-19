import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';
import { ScenarioParameterService } from 'app/entities/scenario-parameter/service/scenario-parameter.service';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, sortStateSignal } from 'app/shared/sort';
import { sort } from 'app/core/util/operators';

@Component({
  standalone: true,
  selector: 'app-scenario-parameters-table',
  templateUrl: './scenario-parameters-table.component.html',
  imports: [RouterModule, SharedModule, SortByDirective, SortDirective],
})
export class ScenarioParametersTableComponent implements OnInit {
  private predicate = 'parameterId';

  @Input()
  sortState = sortStateSignal({ predicate: this.predicate });

  sortedParameters: IScenarioParameter[] | null = null;

  constructor(protected scenarioParameterService: ScenarioParameterService) {}

  ngOnInit(): void {
    this.sortParameters();
  }

  @Input() set parameters(parameters: IScenarioParameter[] | null) {
    this.sortedParameters = parameters ? parameters.slice() : [];
    this.sortParameters();
  }

  trackId = (_index: number, item: IScenarioParameter): number => this.scenarioParameterService.getScenarioParameterIdentifier(item);

  sortParameters(): void {
    sort(this.sortedParameters, this.sortState(), this.predicate);
  }
}
