import { Routes } from '@angular/router';

import { ScenarioExecutionComponent } from './list/scenario-execution.component';
import { ScenarioExecutionDetailComponent } from './detail/scenario-execution-detail.component';
import ScenarioExecutionResolve from './route/scenario-execution-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const scenarioExecutionRoute: Routes = [
  {
    path: '',
    component: ScenarioExecutionComponent,
    data: {
      defaultSort: 'executionId,' + SortOrder.ASCENDING,
    },
  },
  {
    path: ':executionId/view',
    component: ScenarioExecutionDetailComponent,
    resolve: {
      scenarioExecution: ScenarioExecutionResolve,
    },
  },
];

export default scenarioExecutionRoute;
