import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

import dayjs from 'dayjs/esm';

import {
  IScenarioExecutionStatus,
  scenarioExecutionStatusFromId,
  scenarioExecutionStatusFromName,
} from 'app/entities/scenario-execution/scenario-execution.model';
import SharedModule from 'app/shared/shared.module';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { FilterOptions, IFilterOptions } from 'app/shared/filter';
import { first, Subscription } from 'rxjs';

type ScenarioExecutionFilter = {
  nameContains: string | undefined;
  fromDate: dayjs.Dayjs | undefined;
  toDate: dayjs.Dayjs | undefined;
  statusIn: IScenarioExecutionStatus | undefined;
};

@Component({
  standalone: true,
  selector: 'app-scenario-execution-filter',
  templateUrl: './scenario-execution-filter.component.html',
  imports: [FormsModule, ReactiveFormsModule, SharedModule],
})
export default class ScenarioExecutionFilterComponent implements OnInit {
  filterForm: FormGroup = new FormGroup({
    nameContains: new FormControl(),
    fromDate: new FormControl(),
    toDate: new FormControl(),
    statusIn: new FormControl(),
  });
  valueChanged = false;
  private filterFormValueChanges: Subscription | null = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe(params => this.initializeFilterOptionsFromActivatedRoute(params));
    this.markFilterFormAsUnchanged();
  }

  applyFilter(): void {
    const formValue = this.filterForm.value;
    this.router
      .navigate([], {
        queryParams: {
          ...this.getFilterQueryParameter({
            nameContains: formValue.nameContains,
            fromDate: formValue.fromDate ? dayjs(formValue.fromDate) : undefined,
            toDate: formValue.toDate ? dayjs(formValue.toDate) : undefined,
            statusIn: formValue.statusIn ? scenarioExecutionStatusFromName(formValue.statusIn) : undefined,
          }),
        },
      })
      .catch(() => location.reload());
    this.markFilterFormAsUnchanged();
  }

  resetFilter(): void {
    this.filterForm.reset();
    this.filterForm.markAsPristine();
    this.applyFilter();
    this.markFilterFormAsUnchanged();
  }

  private initializeFilterOptionsFromActivatedRoute(params: ParamMap): void {
    let filters: IFilterOptions = new FilterOptions();
    filters.initializeFromParams(params);
    filters = formatDateTimeFilterOptions(filters);
    filters.filterOptions.map(filterOption => {
      filterOption.name = filterOption.name.split('.')[0];
      switch (filterOption.name) {
        case 'scenarioName':
          this.filterForm.controls.nameContains.setValue(filterOption.values[0]);
          break;
        case 'startDate':
          this.filterForm.controls.fromDate.setValue(filterOption.values[0]);
          break;
        case 'endDate':
          this.filterForm.controls.toDate.setValue(filterOption.values[0]);
          break;
        case 'status':
          this.filterForm.controls.statusIn.setValue(scenarioExecutionStatusFromId(Number(filterOption.values[0])).name);
          break;
      }
    });
    if (filters.filterOptions.length > 0) {
      this.filterForm.markAsDirty();
      this.markFilterFormAsUnchanged();
    }
  }

  private getFilterQueryParameter({ nameContains, fromDate, toDate, statusIn }: ScenarioExecutionFilter): {
    [id: string]: any;
  } {
    return {
      'filter[scenarioName.contains]': nameContains ?? undefined,
      'filter[startDate.greaterThanOrEqual]': fromDate ? fromDate.toJSON() : undefined,
      'filter[endDate.lessThanOrEqual]': toDate ? toDate.toJSON() : undefined,
      'filter[status.in]': statusIn?.id ?? undefined,
    };
  }

  private markFilterFormAsUnchanged(): void {
    if (this.filterFormValueChanges) {
      this.filterFormValueChanges.unsubscribe();
    }

    this.valueChanged = false;
    this.filterFormValueChanges = this.filterForm.valueChanges.pipe(first()).subscribe({ next: () => (this.valueChanged = true) });
  }
}
