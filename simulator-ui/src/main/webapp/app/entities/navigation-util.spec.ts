import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { IFilterOption } from 'app/shared/filter';
import { SortOrder, SortService } from 'app/shared/sort';

import { navigateToWithPagingInformation } from './navigation-util';

describe('navigation-util', () => {
  describe('navigateToWithPagingInformation', () => {
    let router: Router;
    let activatedRoute: ActivatedRoute;
    let sortService: SortService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          provideRouter([]),
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: { queryParams: {} },
            },
          },
          SortService,
        ],
      });

      router = TestBed.inject(Router);
      jest.spyOn(router, 'navigate');
      activatedRoute = TestBed.inject(ActivatedRoute);
      sortService = TestBed.inject(SortService);
    });

    it('should navigate with page and itemsPerPage', () => {
      const page = 2;
      const itemsPerPage = 10;

      navigateToWithPagingInformation(page, itemsPerPage, sortService, {}, router, activatedRoute);

      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [] },
      });
    });

    it('should navigate with sort parameters', () => {
      const page = 1;
      const itemsPerPage = 25;

      navigateToWithPagingInformation(
        page,
        itemsPerPage,
        sortService,
        { predicate: 'name', order: SortOrder.ASCENDING },
        router,
        activatedRoute,
      );

      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: ['name,asc'] },
      });
    });

    it('should navigate with filter options', () => {
      const page = 3;
      const itemsPerPage = 50;

      const categoryQueryParam = 'catecurry';
      const filterOptions: IFilterOption[] = [
        { name: 'category', values: ['electronics'], nameAsQueryParam: () => categoryQueryParam },
        { name: 'price', values: ['100-200'], nameAsQueryParam: () => 'price' },
      ];

      navigateToWithPagingInformation(page, itemsPerPage, sortService, {}, router, activatedRoute, filterOptions);

      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [], [categoryQueryParam]: ['electronics'], price: ['100-200'] },
      });
    });
  });
});
