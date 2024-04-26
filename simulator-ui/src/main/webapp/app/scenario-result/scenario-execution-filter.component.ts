import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, ParamMap, Params, Router } from '@angular/router';

import { debounceTime, Subscription } from 'rxjs';

import dayjs from 'dayjs/esm';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';

import SharedModule from 'app/shared/shared.module';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { FilterOptions, IFilterOptions } from 'app/shared/filter';

import {
  IScenarioExecutionStatus,
  scenarioExecutionStatusFromId,
  scenarioExecutionStatusFromName,
} from 'app/entities/scenario-execution/scenario-execution.model';

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
export default class ScenarioExecutionFilterComponent implements OnInit, OnDestroy {
  filterForm: FormGroup = new FormGroup({
    nameContains: new FormControl(),
    fromDate: new FormControl(),
    toDate: new FormControl(),
    statusIn: new FormControl(),
  });
  private filterFormValueChanges: Subscription | null = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe(params => this.initializeFilterOptionsFromActivatedRoute(params)).unsubscribe();
    this.automaticApplyOnFormValueChanges();
  }

  ngOnDestroy(): void {
    this.filterFormValueChanges?.unsubscribe();
  }

  protected applyFilter(formValue = this.filterForm.value): void {
    this.activatedRoute.queryParams
      .subscribe(queryParams => {
        this.router
          .navigate([], {
            queryParams: this.mergeParamsRemoveUndefinedValues(
              queryParams,
              this.getFilterQueryParameter({
                nameContains: formValue.nameContains,
                fromDate: formValue.fromDate ? dayjs(formValue.fromDate) : undefined,
                toDate: formValue.toDate ? dayjs(formValue.toDate) : undefined,
                statusIn: formValue.statusIn ? scenarioExecutionStatusFromName(formValue.statusIn) : undefined,
              }),
            ),
          })
          .catch(() => location.reload());
      })
      .unsubscribe();
  }

  protected resetFilter(): void {
    this.filterForm.reset();
    this.filterForm.markAsPristine();
    this.applyFilter();
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
    }
  }

  private automaticApplyOnFormValueChanges(): void {
    this.filterFormValueChanges = this.filterForm.valueChanges.pipe(debounceTime(DEBOUNCE_TIME_MILLIS)).subscribe({
      next: values => this.applyFilter(values),
    });
  }

  private getFilterQueryParameter({ nameContains, fromDate, toDate, statusIn }: ScenarioExecutionFilter): {
    [id: string]: any;
  } {
    return {
      'filter[scenarioName.contains]': nameContains ?? undefined,
      'filter[startDate.greaterThanOrEqual]': fromDate ? fromDate.toJSON() : undefined,
      'filter[endDate.lessThanOrEqual]': toDate ? toDate.toJSON() : undefined,
      'filter[status.equals]': statusIn?.id ?? undefined,
    };
  }

  private mergeParamsRemoveUndefinedValues(queryParams: Params, filterQueryParams: { [id: string]: any }): { [id: string]: any } {
    return Object.fromEntries(
      Object.entries({
        ...queryParams,
        ...filterQueryParams,
      }).filter(([_, value]) => !!value),
    );
  }
}
