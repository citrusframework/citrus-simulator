import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, ParamMap, Params, Router } from '@angular/router';

import { debounceTime, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';

import dayjs from 'dayjs/esm';

import { DEBOUNCE_TIME_MILLIS } from 'app/config/input.constants';

import SharedModule from 'app/shared/shared.module';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { FilterOptions, IFilterOptions } from 'app/shared/filter';

import { ITestResultStatus, testResultStatusFromId, testResultStatusFromName } from 'app/entities/test-result/test-result.model';

import HeaderFilterDialogComponent, { ComparatorType, HeaderFilter, ValueType } from './header-filter-dialog.component';
import HeaderFilterHelpDialogComponent from './header-filter-help-dialog.component';

type ScenarioExecutionFilter = {
  nameContains: string | undefined;
  fromDate: dayjs.Dayjs | undefined;
  toDate: dayjs.Dayjs | undefined;
  statusIn: ITestResultStatus | undefined;
  headerFilter: string | undefined;
};

export const invalidHeaderFilterPatternValidator =
  (): ValidatorFn =>
  (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const isValid =
      control.value
        .split(/;\s{1}|;/)
        .map((headerFilters: string) => headerFilterStringToForm(headerFilters))
        .indexOf(false) < 0;
    return isValid ? null : { invalidHeaderFilterPattern: { value: control.value } };
  };

export const headerFilterFormToString = (headerFilter: FormGroup<HeaderFilter>): string => {
  let filterString = '';

  if (headerFilter.get('key')?.value) {
    filterString += `${headerFilter.get('key')?.value}${headerFilter.get('valueComparator')?.value}`;
  }

  return `${filterString}${headerFilter.get('value')?.value}`;
};

export const headerFilterStringToForm = (headerFilter: string): FormGroup<HeaderFilter> | false => {
  const formGroup = new FormGroup<HeaderFilter>({
    key: new FormControl<string>(''),
    keyComparator: new FormControl<ComparatorType>({ value: ComparatorType.EQUALS, disabled: true }),
    value: new FormControl<string>(''),
    valueType: new FormControl<ValueType>(ValueType.LITERAL),
    valueComparator: new FormControl<ComparatorType>(ComparatorType.EQUALS),
  });

  const parts = headerFilter.split(/([=~]|[<>]=?)/g);
  if (!headerFilter || !/^\w?(([\w-]+)[=~]?[ \w,/.:()-]*|([\w-]+)[<>]=?\d+)$/.test(headerFilter) || parts.length === 2) {
    return false;
  }

  if (parts.length === 1) {
    formGroup.controls.value.setValue(parts[0]);
    formGroup.controls.value.markAsDirty();
  } else {
    formGroup.controls.key.setValue(parts[0]);
    formGroup.controls.key.markAsDirty();

    formGroup.controls.valueComparator.setValue(parts[1] as ComparatorType);
    formGroup.controls.valueComparator.markAsDirty();

    formGroup.controls.value.setValue(parts[2]);
    formGroup.controls.value.markAsDirty();
  }

  return formGroup;
};

@Component({
  standalone: true,
  selector: 'app-scenario-execution-filter',
  templateUrl: './scenario-execution-filter.component.html',
  styleUrls: ['./scenario-execution-filter.component.scss'],
  imports: [SharedModule, FormsModule, ReactiveFormsModule, MdbFormsModule],
})
export default class ScenarioExecutionFilterComponent implements OnInit, OnDestroy {
  filterForm: FormGroup = new FormGroup({
    nameContains: new FormControl<string>(''),
    fromDate: new FormControl(),
    toDate: new FormControl(),
    statusIn: new FormControl(),
    headerFilter: new FormControl<string>('', [invalidHeaderFilterPatternValidator()]),
  });
  private filterFormValueChanges: Subscription | null = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private changeDetector: ChangeDetectorRef,
    private router: Router,
    public modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe(params => this.initializeFilterOptionsFromActivatedRoute(params)).unsubscribe();
    this.automaticApplyOnFormValueChanges();
    this.changeDetector.detectChanges();
  }

  ngOnDestroy(): void {
    this.filterFormValueChanges?.unsubscribe();
  }

  resetFilter(): void {
    this.filterForm.reset();
    this.filterForm.markAsPristine();
    this.applyFilter();
  }

  openHelpModal(): void {
    this.modalService.open(HeaderFilterHelpDialogComponent, { size: 'm' });
  }

  openHeaderFilterModal(): void {
    const modalRef = this.modalService.open(HeaderFilterDialogComponent, { size: 'xl', backdrop: 'static' });
    modalRef.componentInstance.headerFilters = this.filterForm
      .get('headerFilter')
      ?.value.split(/; |;/)
      .map((headerFilters: string) => headerFilterStringToForm(headerFilters))
      .filter((headerFilters: FormGroup<HeaderFilter> | false) => !!headerFilters);
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(headerFilters => !!headerFilters),
        map((headerFilters: FormGroup<HeaderFilter>[]) =>
          headerFilters.map(headerFilter => headerFilterFormToString(headerFilter)).join('; '),
        ),
      )
      .subscribe(headerFilterString => {
        this.filterForm.controls.headerFilter.setValue(headerFilterString);
        this.filterForm.controls.headerFilter.markAsDirty();
      });
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
                fromDate: formValue.fromDate ? dayjs.utc(formValue.fromDate) : undefined,
                toDate: formValue.toDate ? dayjs.utc(formValue.toDate) : undefined,
                statusIn: formValue.statusIn ? testResultStatusFromName(formValue.statusIn) : undefined,
                headerFilter: formValue.headerFilter,
              }),
            ),
          })
          .catch(() => location.reload());
      })
      .unsubscribe();
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
          this.filterForm.controls.nameContains.markAsDirty();
          break;
        case 'startDate':
          this.filterForm.controls.fromDate.setValue(filterOption.values[0]);
          this.filterForm.controls.fromDate.markAsDirty();
          break;
        case 'endDate':
          this.filterForm.controls.toDate.setValue(filterOption.values[0]);
          this.filterForm.controls.toDate.markAsDirty();
          break;
        case 'status':
          this.filterForm.controls.statusIn.setValue(testResultStatusFromId(Number(filterOption.values[0])).name);
          this.filterForm.controls.statusIn.markAsDirty();
          break;
        case 'headers':
          this.filterForm.controls.headerFilter.setValue(filterOption.values[0]);
          this.filterForm.controls.headerFilter.markAsDirty();
          break;
      }
    });
  }

  private automaticApplyOnFormValueChanges(): void {
    this.filterFormValueChanges = this.filterForm.valueChanges.pipe(debounceTime(DEBOUNCE_TIME_MILLIS)).subscribe({
      next: values => this.applyFilter(values),
    });
  }

  private mergeParamsRemoveUndefinedValues(queryParams: Params, filterQueryParams: Record<string, any>): Record<string, any> {
    return Object.fromEntries(
      Object.entries({
        ...queryParams,
        ...filterQueryParams,
      }).filter(([_, value]) => !!value),
    );
  }

  private getFilterQueryParameter({
    nameContains,
    fromDate,
    toDate,
    statusIn,
    headerFilter,
  }: ScenarioExecutionFilter): Record<string, any> {
    return {
      'filter[scenarioName.contains]': nameContains ?? undefined,
      'filter[startDate.greaterThanOrEqual]': fromDate ? fromDate.toJSON() : undefined,
      'filter[endDate.lessThanOrEqual]': toDate ? toDate.toJSON() : undefined,
      'filter[status.equals]': statusIn?.id ?? undefined,
      'filter[headers]': headerFilter ?? undefined,
    };
  }
}
