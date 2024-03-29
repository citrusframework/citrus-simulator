import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';

import { IMessage } from '../message.model';
import MessageHeaderTableComponent from '../../message-header/list/message-header-table.component';

@Component({
  standalone: true,
  selector: 'app-message-detail',
  templateUrl: './message-detail.component.html',
  imports: [
    SharedModule,
    RouterModule,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    MessageHeaderTableComponent,
    MessageHeaderTableComponent,
  ],
})
export class MessageDetailComponent {
  @Input() message: IMessage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
