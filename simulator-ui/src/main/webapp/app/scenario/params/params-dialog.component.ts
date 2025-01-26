import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import SharedModule from '../../shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-params-dialog',
  templateUrl: './params-dialog.component.html',
  imports: [FormsModule, SharedModule],
})
export class ParamsDialogComponent {
  params: { name: string; value: string }[] = [];

  constructor(public activeModal: NgbActiveModal) {}

  launch(): void {
    this.activeModal.close(this.params);
  }
}
