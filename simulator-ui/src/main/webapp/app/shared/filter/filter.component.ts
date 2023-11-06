import { Component, Input } from '@angular/core';

import SharedModule from 'app/shared/shared.module';

import { IFilterOptions } from './filter.model';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './filter.component.html',
})
export default class FilterComponent {
  @Input() filters!: IFilterOptions;

  clearAllFilters(): void {
    this.filters.clear();
  }

  clearFilter(filterName: string, value: string): void {
    this.filters.removeFilter(filterName, value);
  }
}
