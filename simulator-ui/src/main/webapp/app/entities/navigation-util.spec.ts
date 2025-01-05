import { NgZone } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { IFilterOption } from '../shared/filter';
import type { SortState } from '../shared/sort';
import { SortService } from '../shared/sort';

import { navigateToWithPagingInformation } from './navigation-util';
import { EntityOrder } from '../config/navigation.constants';

describe('navigation-util', () => {
  describe('navigateToWithPagingInformation', () => {
    const sortState: SortState = { predicate: 'id', order: EntityOrder.ASCENDING };

    let ngZone: NgZone;
    let router: Router;
    let activatedRoute: ActivatedRoute;
    let sortService: jest.Mocked<SortService>;

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
        ],
      });

      // eslint-disable-next-line @typescript-eslint/no-unsafe-return
      ngZone = { run: (invocation: any) => invocation() } as unknown as NgZone;
      jest.spyOn(ngZone, 'run');
      router = TestBed.inject(Router);
      jest.spyOn(router, 'navigate');
      activatedRoute = TestBed.inject(ActivatedRoute);
      sortService = { buildSortParam: jest.fn() } as Partial<SortService> as jest.Mocked<SortService>;
    });

    it('should navigate with page and itemsPerPage', () => {
      const page = 2;
      const itemsPerPage = 10;

      sortService.buildSortParam.mockReturnValueOnce([]);

      navigateToWithPagingInformation(page, itemsPerPage, activatedRoute, ngZone, router, sortService, sortState);

      expect(sortService.buildSortParam).toHaveBeenCalledWith(sortState);
      expect(ngZone.run).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [] },
      });
    });

    it('should navigate with sort parameters', () => {
      const page = 1;
      const itemsPerPage = 25;

      sortService.buildSortParam.mockReturnValueOnce(['name,asc']);

      navigateToWithPagingInformation(page, itemsPerPage, activatedRoute, ngZone, router, sortService, sortState);

      expect(sortService.buildSortParam).toHaveBeenCalledWith(sortState);
      expect(ngZone.run).toHaveBeenCalled();
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

      sortService.buildSortParam.mockReturnValueOnce([]);

      navigateToWithPagingInformation(page, itemsPerPage, activatedRoute, ngZone, router, sortService, sortState, filterOptions);

      expect(ngZone.run).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [], [categoryQueryParam]: ['electronics'], price: ['100-200'] },
      });
    });
  });
});
