import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import { TestParameterComponent } from './list/test-parameter.component';
import { TestParameterDetailComponent } from './detail/test-parameter-detail.component';
import TestParameterResolve from './route/test-parameter-routing-resolve.service';

const testParameterRoute: Routes = [
  {
    path: '',
    component: TestParameterComponent,
    data: {
      defaultSort: 'createdDate,' + EntityOrder.ASCENDING,
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
