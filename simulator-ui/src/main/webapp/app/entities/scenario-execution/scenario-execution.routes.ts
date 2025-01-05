import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import ScenarioExecutionResolve from './route/scenario-execution-routing-resolve.service';

const scenarioExecutionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scenario-execution.component').then(m => m.ScenarioExecutionComponent),
    data: {
      defaultSort: 'executionId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':executionId/view',
    loadComponent: () => import('./detail/scenario-execution-detail.component').then(m => m.ScenarioExecutionDetailComponent),
    resolve: {
      scenarioExecution: ScenarioExecutionResolve,
    },
  },
];

export default scenarioExecutionRoute;
