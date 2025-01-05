import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';

import { ScenarioComponent } from './list/scenario.component';
import { ScenarioDetailComponent } from './detail/scenario-detail.component';

import ScenarioResolve from './route/scenario-parameter-routing-resolve.service';

const scenarioRoute: Routes = [
  {
    path: '',
    component: ScenarioComponent,
    data: {
      defaultSort: 'id,' + EntityOrder.ASCENDING,
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

export default scenarioRoute;
