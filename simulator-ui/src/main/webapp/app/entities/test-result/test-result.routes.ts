import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import TestResultResolve from './route/test-result-routing-resolve.service';

const testResultRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/test-result.component').then(m => m.TestResultComponent),
    data: {
      defaultSort: 'id,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/test-result-detail.component').then(m => m.TestResultDetailComponent),
    resolve: {
      testResult: TestResultResolve,
    },
  },
];

export default testResultRoute;
