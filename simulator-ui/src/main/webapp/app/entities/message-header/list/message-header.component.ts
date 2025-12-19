import { HttpHeaders } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';

import { combineLatest, Observable, switchMap, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';

import SharedModule from 'app/shared/shared.module';
import { formatDateTimeFilterOptions } from 'app/shared/date/format-date-time-filter-options';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ItemCount } from 'app/shared/pagination';

import { EntityArrayResponseType, MessageHeaderService } from '../service/message-header.service';
import { IMessageHeader } from '../message-header.model';

import MessageHeaderTableComponent from './message-header-table.component';

import { navigateToWithPagingInformation } from '../../navigation-util';
import { SortService, SortState, sortStateSignal } from '../../../shared/sort';

@Component({
  standalone: true,
  selector: 'app-message-header',
  templateUrl: './message-header.component.html',
  imports: [FormsModule, SharedModule, Filter, ItemCount, MessageHeaderTableComponent],
})
export class MessageHeaderComponent implements OnInit {
  messageHeaders?: IMessageHeader[];
  isLoading = false;

  sortState = sortStateSignal({ predicate: 'headerId' });

  displayFilters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly sortService = inject(SortService);

  protected messageHeaderService = inject(MessageHeaderService);

  private filters: IFilterOptions = new FilterOptions();

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

  navigateToPage(page = this.page): void {
    this.handleNavigation(page);
  }

  protected navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event);
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
    this.messageHeaders = this.fillComponentAttributesFromResponseBody(response.body);
  }

  protected fillComponentAttributesFromResponseBody(data: IMessageHeader[] | null): IMessageHeader[] {
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
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };

    this.filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });

    return this.messageHeaderService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(
    page: number,
    sortState: SortState = this.sortState(),
    filterOptions: IFilterOption[] = this.filters.filterOptions,
  ): void {
    navigateToWithPagingInformation(
      page,
      this.itemsPerPage,
      this.sortService,
      sortState,

      this.router,
      this.activatedRoute,
      filterOptions,
    );
  }
}
