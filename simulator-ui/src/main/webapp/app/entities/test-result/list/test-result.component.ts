import { Component, inject, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { Filter, FilterOptions, IFilterOptions } from 'app/shared/filter';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, sortStateSignal } from 'app/shared/sort';

import { EntityArrayResponseType, TestResultService } from '../service/test-result.service';
import { ITestResult, ITestResultStatus } from '../test-result.model';

import { navigateToWithPagingInformation } from '../../navigation-util';

@Component({
  standalone: true,
  selector: 'app-test-result',
  templateUrl: './test-result.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatetimePipe, Filter, ItemCount],
})
export class TestResultComponent implements OnInit {
  testResults?: ITestResult[];
  isLoading = false;

  sortState = sortStateSignal({ predicate: 'id' });

  displayFilters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly sortService = inject(SortService);

  protected readonly testResultService = inject(TestResultService);

  private filters: IFilterOptions = new FilterOptions();

  trackId = (_index: number, item: ITestResult): number => this.testResultService.getTestResultIdentifier(item);

  ngOnInit(): void {
    this.load();

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, filterOptions));
  }

  load(): void {
    this.loadFromBackendWithRouteInformation().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page);
  }

  protected loadFromBackendWithRouteInformation(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend()),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
    this.displayFilters = formatDateTimeFilterOptions(this.filters);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    this.testResults = this.fillComponentAttributesFromResponseBody(response.body);
  }

  protected fillComponentAttributesFromResponseBody(data: ITestResult[] | null): ITestResult[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = this.page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    this.filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });

    return this.testResultService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, filterOptions = this.filters.filterOptions): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      this.sortService,
      this.sortState(),
      this.router,
      this.activatedRoute,
      filterOptions,
    );
  }

  protected getStatusBadgeClass(status: ITestResultStatus): string {
    switch (status.name) {
      case 'FAILURE':
        return 'bg-danger';
      case 'SKIP':
        return 'bg-danger';
      case 'SUCCESS':
        return 'bg-success';
      default:
        return 'bg-info';
    }
  }
}
