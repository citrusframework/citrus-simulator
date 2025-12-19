import { Routes } from '@angular/router';

import ScenarioResultComponent from './scenario-result.component';
import { SortOrder } from 'app/shared/sort';

const routes: Routes = [
  {
    path: '',
    component: ScenarioResultComponent,
    data: {
      defaultSort: 'executionId,' + SortOrder.ASCENDING,
    },
  },
];

export default routes;
