import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { UserPreferenceService } from 'app/core/config/user-preference.service';

@Component({
  standalone: true,
  selector: 'app-select-page-size',
  templateUrl: './select-page-size.component.html',
  imports: [ReactiveFormsModule],
})
export default class SelectPageSizeComponent implements OnInit {
  @Input()
  public key: string | null = null;

  @Output()
  public pageSizeChanged = new EventEmitter<number>();

  protected pageSizeForm: FormGroup = new FormGroup({
    pageSize: new FormControl(ITEMS_PER_PAGE),
  });

  constructor(private userPreferenceService: UserPreferenceService) {}

  ngOnInit(): void {
    if (this.key) {
      this.pageSizeForm.controls.pageSize.setValue(this.userPreferenceService.getPageSize(this.key));
    }

    this.pageSizeForm.valueChanges.subscribe({
      next: ({ pageSize }) => {
        if (this.key) {
          this.userPreferenceService.setPageSize(this.key, pageSize);
        }

        this.pageSizeChanged.next(pageSize);
      },
    });
  }
}
