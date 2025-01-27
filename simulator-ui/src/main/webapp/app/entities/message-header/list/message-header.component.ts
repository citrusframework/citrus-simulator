import { HttpHeaders } from '@angular/common/http';
import { Component, NgZone, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, EntityOrder, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

import { SortParameters } from 'app/core/util/sort-parameters.type';

import SharedModule from 'app/shared/shared.module';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ItemCountComponent } from 'app/shared/pagination';

import { EntityArrayResponseType, MessageHeaderService } from '../service/message-header.service';
import { IMessageHeader } from '../message-header.model';

import MessageHeaderTableComponent from './message-header-table.component';

import { navigateToWithPagingInformation } from '../../navigation-util';

@Component({
  standalone: true,
  selector: 'app-message-header',
  templateUrl: './message-header.component.html',
  imports: [FormsModule, SharedModule, FilterComponent, ItemCountComponent, MessageHeaderTableComponent],
})
export class MessageHeaderComponent implements OnInit {
  messageHeaders?: IMessageHeader[];
  isLoading = false;

  predicate = 'headerId';
  ascending = true;

  displayFilters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  private filters: IFilterOptions = new FilterOptions();

  constructor(
    private ngZone: NgZone,
    protected messageHeaderService: MessageHeaderService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
  ) {}

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

  navigateToWithComponentValues({ predicate, ascending }: SortParameters): void {
    this.predicate = predicate;
    this.ascending = ascending;

    this.handleNavigation(this.page, predicate, ascending, this.filters.filterOptions);
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
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.messageHeaders = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IMessageHeader[] | null): IMessageHeader[] {
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
      eagerload: true,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    filterOptions?.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.messageHeaderService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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
}
