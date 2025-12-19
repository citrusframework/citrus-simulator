import { IFilterOption } from 'app/shared/filter';
import { ActivatedRoute, Router } from '@angular/router';
import { SortService, SortState } from 'app/shared/sort';

export const navigateToWithPagingInformation = (
  page: number,
  itemsPerPage: number,
  sortService: SortService,
  sortState: SortState,
  router: Router,
  activatedRoute: ActivatedRoute,
  filterOptions?: IFilterOption[],
): void => {
  const queryParamsObj = {
    page,
    size: itemsPerPage,
    sort: sortService.buildSortParam(sortState),
  };

  filterOptions?.forEach(filterOption => {
    // @ts-expect-error TS7053: Element implicitly has an any type because expression of type string can't be used to index type
    queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
  });

  router.navigate(['./'], {
    relativeTo: activatedRoute,
    queryParams: queryParamsObj,
  });

  // ngZone
  //   .run(() =>
  //     router.navigate(['./'], {
  //       relativeTo: activatedRoute,
  //       queryParams: queryParamsObj,
  //     }),
  //   )
  //   .catch(() => location.reload());
};
