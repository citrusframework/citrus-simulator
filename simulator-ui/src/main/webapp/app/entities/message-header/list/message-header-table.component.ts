import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SortParameters } from 'app/core/util/sort-parameters.type';
import { sort } from 'app/core/util/operators';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { SortByDirective, SortDirective } from 'app/shared/sort';

import { IMessageHeader } from '../message-header.model';
import { MessageHeaderService } from '../service/message-header.service';

@Component({
  standalone: true,
  selector: 'app-message-header-table',
  templateUrl: './message-header-table.component.html',
  imports: [RouterModule, SharedModule, FormatMediumDatetimePipe, SortDirective, SortByDirective],
})
export default class MessageHeaderTableComponent implements OnInit {
  @Input()
  ascending = true;

  @Input()
  predicate = 'headerId';

  @Output()
  sortChange = new EventEmitter<SortParameters>();

  sortedMessageHeaders: IMessageHeader[] | null = null;

  @Input()
  standalone = false;

  constructor(private messageHeaderService: MessageHeaderService) {}

  ngOnInit(): void {
    // @ts-expect-error: string-property identifier
    this.sortedMessageHeaders?.sort((a: IMessageHeader, b: IMessageHeader) => (a[this.predicate] as number) - b[this.predicate]);
  }

  @Input() set messageHeaders(messageHeaders: IMessageHeader[] | null) {
    this.sortedMessageHeaders = messageHeaders ? messageHeaders.slice() : [];

    if (this.standalone) {
      this.emitSortChange();
    }
  }

  trackId = (_index: number, item: IMessageHeader): number => this.messageHeaderService.getMessageHeaderIdentifier(item);

  protected emitSortChange(): void {
    if (this.standalone) {
      sort(this.sortedMessageHeaders, this.predicate, this.ascending);
    } else {
      this.sortChange.emit({ predicate: this.predicate, ascending: this.ascending });
    }
  }
}
