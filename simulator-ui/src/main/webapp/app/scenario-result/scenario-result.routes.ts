import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';

import ScenarioResultComponent from './scenario-result.component';

const scenarioExecutionRoute: Routes = [
  {
    path: '',
    component: ScenarioResultComponent,
    data: {
      defaultSort: 'executionId,' + EntityOrder.ASCENDING,
    },
  },
];

export default scenarioExecutionRoute;
