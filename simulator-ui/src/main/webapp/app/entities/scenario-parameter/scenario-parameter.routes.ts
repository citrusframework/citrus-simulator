import { Routes } from '@angular/router';

import { ScenarioParameterComponent } from './list/scenario-parameter.component';
import { ScenarioParameterDetailComponent } from './detail/scenario-parameter-detail.component';
import ScenarioParameterResolve from './route/scenario-parameter-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const scenarioParameterRoute: Routes = [
  {
    path: '',
    component: ScenarioParameterComponent,
    data: {
      defaultSort: 'parameterId,' + SortOrder.ASCENDING,
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
