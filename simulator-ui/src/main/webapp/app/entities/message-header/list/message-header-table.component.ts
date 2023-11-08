import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { SortByDirective, SortDirective } from 'app/shared/sort';

import { IMessageHeader } from '../message-header.model';
import { MessageHeaderService } from '../service/message-header.service';

export type MessageHeaderSort = { predicate: string; ascending: boolean };

@Component({
  standalone: true,
  selector: 'app-message-header-table',
  templateUrl: './message-header-table.component.html',
  imports: [RouterModule, SharedModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, SortDirective, SortByDirective],
})
export default class MessageHeaderTableComponent implements OnInit {
  @Input()
  ascending = true;

  @Input()
  predicate = 'headerId';

  @Output()
  sortChange = new EventEmitter<MessageHeaderSort>();

  @Input()
  messageHeaders: IMessageHeader[] | null = null;

  @Input()
  fullDetails = true;

  constructor(private messageHeaderService: MessageHeaderService) {}

  ngOnInit(): void {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    this.messageHeaders?.sort((a: IMessageHeader, b: IMessageHeader) => (a[this.predicate] as number) - b[this.predicate]);
  }

  trackId = (_index: number, item: IMessageHeader): number => this.messageHeaderService.getMessageHeaderIdentifier(item);

  protected emitSortChange(): void {
    this.sortChange.emit({ predicate: this.predicate, ascending: this.ascending });
  }
}
