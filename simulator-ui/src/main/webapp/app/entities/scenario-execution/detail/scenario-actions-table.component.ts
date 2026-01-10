import { Component, Input, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import { sort } from 'app/core/util/operators';

import SharedModule from 'app/shared/shared.module';
import FormatMediumDatetimePipe from 'app/shared/date/format-medium-datetime.pipe';
import { SortByDirective, SortDirective, sortStateSignal } from 'app/shared/sort';

import { IScenarioAction } from 'app/entities/scenario-action/scenario-action.model';
import { ScenarioActionService } from 'app/entities/scenario-action/service/scenario-action.service';

const predicate = 'actionId';

@Component({
  standalone: true,
  selector: 'app-scenario-actions-table',
  templateUrl: './scenario-actions-table.component.html',
  imports: [RouterModule, SharedModule, FormatMediumDatetimePipe, SortDirective, SortByDirective],
})
export class ScenarioActionsTableComponent implements OnInit {
  @Input()
  sortState = sortStateSignal({ predicate });

  sortedActions: IScenarioAction[] | null = null;

  protected scenarioActionService = inject(ScenarioActionService);

  ngOnInit(): void {
    this.sortActions();
  }

  @Input() set actions(actions: IScenarioAction[] | null) {
    this.sortedActions = actions ? actions.slice() : [];
    this.sortActions();
  }

  trackId = (_index: number, item: IScenarioAction): number => this.scenarioActionService.getScenarioActionIdentifier(item);

  sortActions(): void {
    sort(this.sortedActions, this.sortState(), predicate);
  }
}
