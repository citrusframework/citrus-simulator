import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  templateUrl: './header-filter-help-dialog.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export default class HeaderFilterHelpDialogComponent {
  protected readonly activeModal = inject(NgbActiveModal);

  protected cancel(): void {
    this.activeModal.dismiss();
  }
}
