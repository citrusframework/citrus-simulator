import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { ScenarioParameterComponent } from './list/scenario-parameter.component';
import { ScenarioParameterDetailComponent } from './detail/scenario-parameter-detail.component';
import ScenarioParameterResolve from './route/scenario-parameter-routing-resolve.service';

const scenarioParameterRoute: Routes = [
  {
    path: '',
    component: ScenarioParameterComponent,
    data: {
      defaultSort: 'parameterId,' + ASC,
    },
  },
  {
    path: ':parameterId/view',
    component: ScenarioParameterDetailComponent,
    resolve: {
      scenarioParameter: ScenarioParameterResolve,
    },
  },
];

export default scenarioParameterRoute;
