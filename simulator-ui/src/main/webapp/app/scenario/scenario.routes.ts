import { Routes } from '@angular/router';

import { ScenarioComponent } from './list/scenario.component';
import { ScenarioDetailComponent } from './detail/scenario-detail.component';

import ScenarioResolve from './route/scenario-parameter-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const routes: Routes = [
  {
    path: '',
    component: ScenarioComponent,
    data: {
      defaultSort: 'id,' + SortOrder.ASCENDING,
    },
  },
  {
    path: ':name/:type/view',
    component: ScenarioDetailComponent,
    resolve: {
      scenarioParameters: ScenarioResolve,
    },
  },
];

export default routes;
