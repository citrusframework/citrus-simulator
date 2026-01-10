import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, SORT } from 'app/config/navigation.constants';

import SharedModule from 'app/shared/shared.module';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, SortState, sortStateSignal } from 'app/shared/sort';

import { EntityArrayResponseType, ScenarioExecutionService } from '../service/scenario-execution.service';
import { IScenarioExecution } from '../scenario-execution.model';

import { navigateToWithPagingInformation } from '../../navigation-util';
import { ITestResultStatus } from '../../test-result/test-result.model';

const USER_PREFERENCES_KEY = 'scenario';

@Component({
  standalone: true,
  selector: 'app-scenario-execution',
  templateUrl: './scenario-execution.component.html',
  styleUrls: ['./scenario-execution.component.scss'],
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatetimePipe, Filter, ItemCount],
})
export class ScenarioExecutionComponent implements OnInit {
  userPreferencesKey = USER_PREFERENCES_KEY;

  @Input() hideTitle = false;

  @Output() sortChange = new EventEmitter<SortState>();

  scenarioExecutions?: IScenarioExecution[];
  isLoading = false;

  sortState = sortStateSignal({ predicate: 'executionId' });

  displayFilters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly sortService = inject(SortService);

  protected readonly scenarioExecutionService = inject(ScenarioExecutionService);

  private filters: IFilterOptions = new FilterOptions();

  trackId = (_index: number, item: IScenarioExecution): number => this.scenarioExecutionService.getScenarioExecutionIdentifier(item);

  ngOnInit(): void {
    this.load();
    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  load(): void {
    this.loadFromBackendWithRouteInformation().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.sortChange.emit(this.sortState());
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
    this.scenarioExecutions = this.fillComponentAttributesFromResponseBody(response.body);
  }

  protected fillComponentAttributesFromResponseBody(data: IScenarioExecution[] | null): IScenarioExecution[] {
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

    return this.scenarioExecutionService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(
    page: number,
    sortState: SortState = this.sortState(),
    filterOptions: IFilterOption[] = this.filters.filterOptions,
  ): void {
    navigateToWithPagingInformation(page, this.itemsPerPage, this.sortService, sortState, this.router, this.activatedRoute, filterOptions);
  }

  protected getStatusBadgeClass(status: ITestResultStatus): string {
    switch (status.name) {
      case 'FAILURE':
        return 'bg-danger';
      case 'SUCCESS':
        return 'bg-success';
      default:
        return 'bg-info';
    }
  }
}
