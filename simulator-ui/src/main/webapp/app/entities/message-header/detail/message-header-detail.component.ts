import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMessageHeader } from '../message-header.model';

@Component({
  selector: 'app-message-header-detail',
  templateUrl: './message-header-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MessageHeaderDetailComponent {
  messageHeader = input<IMessageHeader | null>(null);

  previousState(): void {
    window.history.back();
  }
}
