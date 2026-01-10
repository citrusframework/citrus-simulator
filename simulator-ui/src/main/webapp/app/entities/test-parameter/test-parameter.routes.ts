import { Routes } from '@angular/router';

import { TestParameterComponent } from './list/test-parameter.component';
import { TestParameterDetailComponent } from './detail/test-parameter-detail.component';
import TestParameterResolve from './route/test-parameter-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const testParameterRoute: Routes = [
  {
    path: '',
    component: TestParameterComponent,
    data: {
      defaultSort: 'createdDate,' + SortOrder.ASCENDING,
    },
  },
  {
    path: ':testResultId/:key/view',
    component: TestParameterDetailComponent,
    resolve: {
      testParameter: TestParameterResolve,
    },
  },
];

export default testParameterRoute;
