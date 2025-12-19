import { Component, Input, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HighlightAuto } from 'ngx-highlightjs';

import { sort } from 'app/core/util/operators';

import { IMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';

import FormatMediumDatetimePipe from 'app/shared/date/format-medium-datetime.pipe';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, sortStateSignal } from 'app/shared/sort';

const predicate = 'messageId';

@Component({
  standalone: true,
  selector: 'app-scenario-messages-table',
  templateUrl: './scenario-messages-table.component.html',
  imports: [RouterModule, SharedModule, FormatMediumDatetimePipe, SortDirective, SortByDirective, HighlightAuto],
})
export class ScenarioMessagesTableComponent implements OnInit {
  @Input()
  sortState = sortStateSignal({ predicate });

  sortedMessages: IMessage[] | null = null;
  protected messageService = inject(MessageService);

  ngOnInit(): void {
    this.sortMessages();
  }

  @Input() set messages(messages: IMessage[] | null) {
    this.sortedMessages = messages ? messages.slice() : [];
    this.sortMessages();
  }

  trackId = (_index: number, item: IMessage): number => this.messageService.getMessageIdentifier(item);

  sortMessages(): void {
    sort(this.sortedMessages, this.sortState(), predicate);
  }
}
