import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import ScenarioActionResolve from './route/scenario-action-routing-resolve.service';

const scenarioActionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scenario-action.component').then(m => m.ScenarioActionComponent),
    data: {
      defaultSort: 'actionId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':actionId/view',
    loadComponent: () => import('./detail/scenario-action-detail.component').then(m => m.ScenarioActionDetailComponent),
    resolve: {
      scenarioAction: ScenarioActionResolve,
    },
  },
];

export default scenarioActionRoute;
