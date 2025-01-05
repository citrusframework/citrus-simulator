import { IFilterOption } from '../shared/filter';
import { NgZone } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SortService, SortState } from '../shared/sort';

export const navigateToWithPagingInformation = (
  page: number,
  itemsPerPage: number,
  activatedRoute: ActivatedRoute,
  ngZone: NgZone,
  router: Router,
  sortService: SortService,
  sortState: SortState,
  filterOptions?: IFilterOption[],
): void => {
  const queryParamsObj: any = {
    page,
    size: itemsPerPage,
    sort: sortService.buildSortParam(sortState),
  };

  filterOptions?.forEach(filterOption => {
    queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
  });

  ngZone.run(() => {
    router.navigate(['./'], {
      relativeTo: activatedRoute,
      queryParams: queryParamsObj,
    });
  });
};
