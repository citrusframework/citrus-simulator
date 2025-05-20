import { Component, EventEmitter, Input, NgZone, OnInit, Output } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, EntityOrder, SORT } from 'app/config/navigation.constants';

import SharedModule from 'app/shared/shared.module';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { ItemCountComponent } from 'app/shared/pagination';
import { SortByDirective, SortDirective } from 'app/shared/sort';

import { EntityArrayResponseType, ScenarioExecutionService } from '../service/scenario-execution.service';
import { IScenarioExecution } from '../scenario-execution.model';

import { navigateToWithPagingInformation } from '../../navigation-util';
import { ITestResultStatus } from '../../test-result/test-result.model';

@Component({
  standalone: true,
  selector: 'app-scenario-execution',
  templateUrl: './scenario-execution.component.html',
  styleUrls: ['./scenario-execution.component.scss'],
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export class ScenarioExecutionComponent implements OnInit {
  @Input() hideTitle = false;

  @Output() sortChange = new EventEmitter<{ predicate: string; ascending: boolean }>();

  scenarioExecutions?: IScenarioExecution[];
  isLoading = false;

  predicate = 'executionId';
  ascending = true;

  displayFilters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  protected readonly USER_PREFERENCES_KEY = 'scenario';

  private filters: IFilterOptions = new FilterOptions();

  constructor(
    private ngZone: NgZone,
    protected scenarioExecutionService: ScenarioExecutionService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
  ) {}

  trackId = (_index: number, item: IScenarioExecution): number => this.scenarioExecutionService.getScenarioExecutionIdentifier(item);

  ngOnInit(): void {
    this.load();
    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.predicate, this.ascending, filterOptions));
  }

  load(): void {
    this.loadFromBackendWithRouteInformation().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.sortChange.emit({ predicate: this.predicate, ascending: this.ascending });
    this.handleNavigation(this.page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending, this.filters.filterOptions);
  }

  protected loadFromBackendWithRouteInformation(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending, this.filters.filterOptions)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === EntityOrder.ASCENDING;
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

  protected queryBackend(
    page?: number,
    predicate?: string,
    ascending?: boolean,
    filterOptions?: IFilterOption[],
  ): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    filterOptions?.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });

    return this.scenarioExecutionService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filterOptions?: IFilterOption[]): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      () => this.getSortQueryParam(predicate, ascending),
      this.ngZone,
      this.router,
      this.activatedRoute,
      predicate,
      ascending,
      filterOptions,
    );
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? EntityOrder.ASCENDING : EntityOrder.DESCENDING;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
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
