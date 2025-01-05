import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import TestParameterResolve from './route/test-parameter-routing-resolve.service';

const testParameterRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/test-parameter.component').then(m => m.TestParameterComponent),
    data: {
      defaultSort: 'createdDate,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':testResultId/:key/view',
    loadComponent: () => import('./detail/test-parameter-detail.component').then(m => m.TestParameterDetailComponent),
    resolve: {
      testParameter: TestParameterResolve,
    },
  },
];

export default testParameterRoute;
