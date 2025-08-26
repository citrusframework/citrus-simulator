import { Routes } from '@angular/router';
import { EntityOrder } from 'app/config/navigation.constants';
import MessageResolve from './route/message-routing-resolve.service';

const messageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/message.component').then(m => m.MessageComponent),
    data: {
      defaultSort: 'messageId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':messageId/view',
    loadComponent: () => import('./detail/message-detail.component').then(m => m.MessageDetailComponent),
    resolve: {
      message: MessageResolve,
    },
  },
];

export default messageRoute;
