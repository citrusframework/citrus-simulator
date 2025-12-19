import { Component, inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';

export enum ComparatorType {
  EQUALS = '=',
  CONTAINS = '~',
  GREATER_THAN = '>',
  GREATER_THAN_OR_EQUAL_TO = '>=',
  LESS_THAN = '<',
  LESS_THAN_OR_EQUAL_TO = '<=',
}

export enum ValueType {
  LITERAL = 'LITERAL',
  NUMERICAL = 'NUMERICAL',
}

export type HeaderFilter = {
  key: FormControl<string | null>;
  keyComparator: FormControl<ComparatorType | null>;
  value: FormControl<string | null>;
  valueType: FormControl<ValueType | null>;
  valueComparator: FormControl<ComparatorType | null>;
};

@Component({
  standalone: true,
  templateUrl: './header-filter-dialog.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export default class HeaderFilterDialogComponent implements OnInit {
  public headerFilters: FormGroup<HeaderFilter>[] = [];

  protected readonly activeModal = inject(NgbActiveModal);

  ngOnInit(): void {
    if (this.headerFilters.length === 0) {
      this.addNewHeaderFilter();
    }
  }

  protected addNewHeaderFilter(): void {
    this.headerFilters.push(
      new FormGroup<HeaderFilter>({
        key: new FormControl<string>(''),
        keyComparator: new FormControl<ComparatorType>({ value: ComparatorType.EQUALS, disabled: true }),
        value: new FormControl<string>(''),
        valueType: new FormControl<ValueType>(ValueType.LITERAL),
        valueComparator: new FormControl<ComparatorType>(ComparatorType.EQUALS),
      }),
    );
  }

  protected getHeaderValueInputType(index: number): string {
    if (this.headerFilters.length > index) {
      switch (this.headerFilters[index].get('valueType')?.value) {
        case ValueType.LITERAL:
          return 'text';
        case ValueType.NUMERICAL:
          return 'number';
      }
    }

    return 'text';
  }

  protected removeHeaderFilter(index: number): void {
    if (this.headerFilters.length > index && index !== 0) {
      this.headerFilters.splice(index, 1);
    }
  }

  protected submit(): void {
    this.activeModal.close(this.headerFilters);
  }

  protected cancel(): void {
    this.activeModal.dismiss();
  }
}
