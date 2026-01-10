import { Routes } from '@angular/router';

import MessageComponent from './list/message.component';
import { MessageDetailComponent } from './detail/message-detail.component';
import MessageResolve from './route/message-routing-resolve.service';
import { SortOrder } from 'app/shared/sort';

const messageRoute: Routes = [
  {
    path: '',
    component: MessageComponent,
    data: {
      defaultSort: 'messageId,' + SortOrder.ASCENDING,
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
