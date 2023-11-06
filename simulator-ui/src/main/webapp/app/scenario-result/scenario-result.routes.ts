import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';

import ScenarioResultComponent from './scenario-result.component';

const scenarioExecutionRoute: Routes = [
  {
    path: '',
    component: ScenarioResultComponent,
    data: {
      defaultSort: 'executionId,' + ASC,
    },
  },
];

export default scenarioExecutionRoute;
