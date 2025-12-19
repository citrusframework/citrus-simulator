import { Routes } from '@angular/router';

import { ScenarioActionComponent } from './list/scenario-action.component';
import { ScenarioActionDetailComponent } from './detail/scenario-action-detail.component';
import ScenarioActionResolve from './route/scenario-action-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const scenarioActionRoute: Routes = [
  {
    path: '',
    component: ScenarioActionComponent,
    data: {
      defaultSort: 'actionId,' + SortOrder.ASCENDING,
    },
  },
  {
    path: ':actionId/view',
    component: ScenarioActionDetailComponent,
    resolve: {
      scenarioAction: ScenarioActionResolve,
    },
  },
];

export default scenarioActionRoute;
