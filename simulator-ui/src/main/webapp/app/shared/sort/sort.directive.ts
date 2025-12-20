import { Directive, model, output } from '@angular/core';

import { SortOrder, SortState } from './sort-state';

@Directive({
  selector: '[jhiSort]',
})
export class SortDirective {
  readonly sortState = model.required<SortState>();

  readonly sortChange = output<SortState>();

  sort(field: string): void {
    const { predicate, order } = this.sortState();
    const toggle = (): SortOrder => (order === SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING);
    const newSortState = { predicate: field, order: field === predicate ? toggle() : SortOrder.ASCENDING };
    this.sortState.update(() => newSortState);
    this.sortChange.emit(newSortState);
  }
}
