import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import { TestResultComponent } from './list/test-result.component';
import { TestResultDetailComponent } from './detail/test-result-detail.component';
import TestResultResolve from './route/test-result-routing-resolve.service';

const testResultRoute: Routes = [
  {
    path: '',
    component: TestResultComponent,
    data: {
      defaultSort: 'id,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':id/view',
    component: TestResultDetailComponent,
    resolve: {
      testResult: TestResultResolve,
    },
  },
];

export default testResultRoute;
