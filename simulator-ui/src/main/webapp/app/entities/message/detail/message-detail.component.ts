import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { HighlightAuto } from 'ngx-highlightjs';

import { IMessage } from 'app/entities/message/message.model';
import MessageHeaderTableComponent from 'app/entities/message-header/list/message-header-table.component';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';

@Component({
  standalone: true,
  selector: 'app-message-detail',
  templateUrl: './message-detail.component.html',
  styleUrls: ['./message-detail.component.scss'],
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, MessageHeaderTableComponent, HighlightAuto],
})
export class MessageDetailComponent {
  @Input() message: IMessage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
