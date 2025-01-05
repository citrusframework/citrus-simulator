import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import { MessageHeaderComponent } from './list/message-header.component';
import { MessageHeaderDetailComponent } from './detail/message-header-detail.component';
import MessageHeaderResolve from './route/message-header-routing-resolve.service';

const messageHeaderRoute: Routes = [
  {
    path: '',
    component: MessageHeaderComponent,
    data: {
      defaultSort: 'headerId,' + EntityOrder.ASCENDING,
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
