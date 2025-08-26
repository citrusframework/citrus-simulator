import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import MessageHeaderResolve from './route/message-header-routing-resolve.service';

const messageHeaderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/message-header.component').then(m => m.MessageHeaderComponent),
    data: {
      defaultSort: 'headerId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':headerId/view',
    loadComponent: () => import('./detail/message-header-detail.component').then(m => m.MessageHeaderDetailComponent),
    resolve: {
      messageHeader: MessageHeaderResolve,
    },
  },
];

export default messageHeaderRoute;
