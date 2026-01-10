import { Routes } from '@angular/router';

import { MessageHeaderComponent } from './list/message-header.component';
import { MessageHeaderDetailComponent } from './detail/message-header-detail.component';
import MessageHeaderResolve from './route/message-header-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const messageHeaderRoute: Routes = [
  {
    path: '',
    component: MessageHeaderComponent,
    data: {
      defaultSort: 'headerId,' + SortOrder.ASCENDING,
    },
  },
  {
    path: ':headerId/view',
    component: MessageHeaderDetailComponent,
    resolve: {
      messageHeader: MessageHeaderResolve,
    },
  },
];

export default messageHeaderRoute;
