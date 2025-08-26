import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMessage } from '../message.model';

@Component({
  selector: 'app-message-detail',
  templateUrl: './message-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MessageDetailComponent {
  message = input<IMessage | null>(null);

  previousState(): void {
    window.history.back();
  }
}
