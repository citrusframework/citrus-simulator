import { Routes } from '@angular/router';

import { EntityOrder } from 'app/config/navigation.constants';
import { MessageComponent } from './list/message.component';
import { MessageDetailComponent } from './detail/message-detail.component';
import MessageResolve from './route/message-routing-resolve.service';

const messageRoute: Routes = [
  {
    path: '',
    component: MessageComponent,
    data: {
      defaultSort: 'messageId,' + EntityOrder.ASCENDING,
    },
  },
  {
    path: ':messageId/view',
    component: MessageDetailComponent,
    resolve: {
      message: MessageResolve,
    },
  },
];

export default messageRoute;
