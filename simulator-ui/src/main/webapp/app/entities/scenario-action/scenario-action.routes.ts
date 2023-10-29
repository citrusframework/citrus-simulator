import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { ScenarioActionComponent } from './list/scenario-action.component';
import { ScenarioActionDetailComponent } from './detail/scenario-action-detail.component';
import ScenarioActionResolve from './route/scenario-action-routing-resolve.service';

const scenarioActionRoute: Routes = [
  {
    path: '',
    component: ScenarioActionComponent,
    data: {
      defaultSort: 'actionId,' + ASC,
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
