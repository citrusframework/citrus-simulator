import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import { ScenarioExecutionComponent } from './list/scenario-execution.component';
import { ScenarioExecutionDetailComponent } from './detail/scenario-execution-detail.component';
import ScenarioExecutionResolve from './route/scenario-execution-routing-resolve.service';

const scenarioExecutionRoute: Routes = [
  {
    path: '',
    component: ScenarioExecutionComponent,
    data: {
      defaultSort: 'executionId,' + EntityOrder.ASCENDING,
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
