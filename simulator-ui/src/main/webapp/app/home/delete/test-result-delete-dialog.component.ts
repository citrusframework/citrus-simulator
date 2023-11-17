import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

import SharedModule from 'app/shared/shared.module';
import { TestResultService } from 'app/entities/test-result/service/test-result.service';

@Component({
  standalone: true,
  templateUrl: './test-result-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export default class TestResultDeleteDialogComponent {
  constructor(
    protected activeModal: NgbActiveModal,
    protected testResultService: TestResultService,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(): void {
    this.testResultService.deleteAll().subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
