import { IFilterOption } from '../shared/filter';
import { NgZone } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

export const navigateToWithPagingInformation = (
  page: number,
  itemsPerPage: number,
  getSortQueryParam: (predicate?: string, ascending?: boolean) => string[],
  ngZone: NgZone,
  router: Router,
  activatedRoute: ActivatedRoute,
  predicate?: string,
  ascending?: boolean,
  filterOptions?: IFilterOption[],
): void => {
  const queryParamsObj: any = {
    page,
    size: itemsPerPage,
    sort: getSortQueryParam(predicate, ascending),
  };

  filterOptions?.forEach(filterOption => {
    queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
  });

  ngZone
    .run(() =>
      router.navigate(['./'], {
        relativeTo: activatedRoute,
        queryParams: queryParamsObj,
      }),
    )
    .catch(() => location.reload());
};
