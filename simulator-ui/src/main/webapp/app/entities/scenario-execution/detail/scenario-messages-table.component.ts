import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HighlightAuto } from 'ngx-highlightjs';

import { sort } from 'app/core/util/operators';

import { IMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import FormatMediumDatetimePipe from 'app/shared/date/format-medium-datetime.pipe';
import SharedModule from 'app/shared/shared.module';
import SortByDirective from 'app/shared/sort/sort-by.directive';
import SortDirective from 'app/shared/sort/sort.directive';

@Component({
  standalone: true,
  selector: 'app-scenario-messages-table',
  templateUrl: './scenario-messages-table.component.html',
  imports: [RouterModule, SharedModule, FormatMediumDatetimePipe, SortDirective, SortByDirective, HighlightAuto],
})
export class ScenarioMessagesTableComponent implements OnInit {
  @Input()
  ascending = true;

  @Input()
  predicate = 'messageId';

  sortedMessages: IMessage[] | null = null;

  constructor(protected messageService: MessageService) {}

  ngOnInit(): void {
    this.sortMessages();
  }

  @Input() set messages(messages: IMessage[] | null) {
    this.sortedMessages = messages ? messages.slice() : [];
    this.sortMessages();
  }

  trackId = (_index: number, item: IMessage): number => this.messageService.getMessageIdentifier(item);

  sortMessages(): void {
    sort(this.sortedMessages, this.predicate, this.ascending);
  }
}
