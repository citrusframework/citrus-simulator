import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { sort } from 'app/core/util/operators';

import { IScenarioParameter } from 'app/entities/scenario-parameter/scenario-parameter.model';
import { ScenarioParameterService } from 'app/entities/scenario-parameter/service/scenario-parameter.service';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe } from 'app/shared/date';
import FormatMediumDatetimePipe from 'app/shared/date/format-medium-datetime.pipe';
import SortDirective from 'app/shared/sort/sort.directive';
import SortByDirective from 'app/shared/sort/sort-by.directive';

@Component({
  standalone: true,
  selector: 'app-scenario-parameters-table',
  templateUrl: './scenario-parameters-table.component.html',
  imports: [RouterModule, SharedModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, SortDirective, SortByDirective],
})
export class ScenarioParametersTableComponent implements OnInit {
  @Input()
  ascending = true;

  @Input()
  predicate = 'parameterId';

  sortedParameters: IScenarioParameter[] | null = null;

  constructor(protected scenarioParameterService: ScenarioParameterService) {}

  ngOnInit(): void {
    this.sortParameters();
  }

  @Input() set scenarioParameters(parameters: IScenarioParameter[] | null) {
    this.sortedParameters = parameters ? parameters.slice() : [];
    this.sortParameters();
  }

  trackId = (_index: number, item: IScenarioParameter): number => this.scenarioParameterService.getScenarioParameterIdentifier(item);

  sortParameters(): void {
    sort(this.sortedParameters, this.predicate, this.ascending);
  }
}
