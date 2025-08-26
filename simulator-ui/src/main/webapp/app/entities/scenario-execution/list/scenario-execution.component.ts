import { Component, EventEmitter, inject, Input, NgZone, OnInit, Output, signal } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, Observable, Subscription, tap } from 'rxjs';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, SORT } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { EntityArrayResponseType, ScenarioExecutionService } from '../service/scenario-execution.service';
import { IScenarioExecution } from '../scenario-execution.model';
import { navigateToWithPagingInformation } from '../../navigation-util';
import { ItemCountComponent } from '../../../shared/pagination';
import { ITestResult, ITestResultStatus } from '../../test-result/test-result.model';

@Component({
  selector: 'app-scenario-execution',
  templateUrl: './scenario-execution.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    ItemCountComponent,
    FilterComponent,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
  ],
})
export class ScenarioExecutionComponent implements OnInit {
  @Input() hideTitle = false;

  @Output() sortChange = new EventEmitter<SortState>();

  subscription: Subscription | null = null;
  scenarioExecutions = signal<IScenarioExecution[]>([]);
  isLoading = false;

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly scenarioExecutionService = inject(ScenarioExecutionService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected ngZone = inject(NgZone);

  trackId = (item: IScenarioExecution): number => this.scenarioExecutionService.getScenarioExecutionIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.sortChange.emit(event);
    this.handleNavigation(this.page, event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.scenarioExecutions.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IScenarioExecution[] | null): IScenarioExecution[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page, filters } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.scenarioExecutionService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      this.activatedRoute,
      this.ngZone,
      this.router,
      this.sortService,
      sortState,
      filterOptions,
    );
  }

  getStatusBadgeClass(testResultStatus: ITestResultStatus):string {
      switch (testResultStatus.name) {
        case 'FAILURE':
          return 'bg-danger';
        case 'SUCCESS':
          return 'bg-success';
        default:
          return 'bg-info';
      }
    }
}
