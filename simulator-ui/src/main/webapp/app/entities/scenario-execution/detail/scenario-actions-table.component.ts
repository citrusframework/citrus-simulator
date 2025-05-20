import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { sort } from 'app/core/util/operators';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe } from 'app/shared/date';
import FormatMediumDatetimePipe from 'app/shared/date/format-medium-datetime.pipe';
import SortDirective from 'app/shared/sort/sort.directive';
import SortByDirective from 'app/shared/sort/sort-by.directive';

import { IScenarioAction } from 'app/entities/scenario-action/scenario-action.model';
import { ScenarioActionService } from 'app/entities/scenario-action/service/scenario-action.service';

@Component({
  standalone: true,
  selector: 'app-scenario-actions-table',
  templateUrl: './scenario-actions-table.component.html',
  imports: [RouterModule, SharedModule, FormatMediumDatetimePipe, SortDirective, SortByDirective],
})
export class ScenarioActionsTableComponent implements OnInit {
  @Input()
  ascending = true;

  @Input()
  predicate = 'actionId';

  sortedActions: IScenarioAction[] | null = null;

  constructor(protected scenarioActionService: ScenarioActionService) {}

  ngOnInit(): void {
    this.sortActions();
  }

  @Input() set actions(actions: IScenarioAction[] | null) {
    this.sortedActions = actions ? actions.slice() : [];
    this.sortActions();
  }

  trackId = (_index: number, item: IScenarioAction): number => this.scenarioActionService.getScenarioActionIdentifier(item);

  sortActions(): void {
    sort(this.sortedActions, this.predicate, this.ascending);
  }
}
