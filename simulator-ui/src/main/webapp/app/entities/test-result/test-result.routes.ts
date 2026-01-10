import { Routes } from '@angular/router';

import { TestResultComponent } from './list/test-result.component';
import { TestResultDetailComponent } from './detail/test-result-detail.component';
import TestResultResolve from './route/test-result-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const testResultRoute: Routes = [
  {
    path: '',
    component: TestResultComponent,
    data: {
      defaultSort: 'id,' + SortOrder.ASCENDING,
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
