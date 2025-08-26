import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import ScenarioParameterResolve from './route/scenario-parameter-routing-resolve.service';

const scenarioParameterRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scenario-parameter.component').then(m => m.ScenarioParameterComponent),
    data: {
      defaultSort: 'parameterId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':parameterId/view',
    loadComponent: () => import('./detail/scenario-parameter-detail.component').then(m => m.ScenarioParameterDetailComponent),
    resolve: {
      scenarioParameter: ScenarioParameterResolve,
    },
  },
];

export default scenarioParameterRoute;
