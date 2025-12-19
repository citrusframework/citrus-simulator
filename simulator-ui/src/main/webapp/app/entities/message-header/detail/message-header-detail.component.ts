import { Component, Input, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMessageHeader } from '../message-header.model';

@Component({
  standalone: true,
  selector: 'app-message-header-detail',
  templateUrl: './message-header-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MessageHeaderDetailComponent {
  @Input() messageHeader: IMessageHeader | null = null;

  protected activatedRoute = inject(ActivatedRoute);

  previousState(): void {
    window.history.back();
  }
}
