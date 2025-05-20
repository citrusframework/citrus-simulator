import { NgZone } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { IFilterOption } from '../shared/filter';

import { navigateToWithPagingInformation } from './navigation-util';

describe('navigation-util', () => {
  describe('navigateToWithPagingInformation', () => {
    let ngZone: NgZone;
    let router: Router;
    let activatedRoute: ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [provideRouter([])],
        providers: [
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
    });

    it('should navigate with page and itemsPerPage', () => {
      const page = 2;
      const itemsPerPage = 10;

      navigateToWithPagingInformation(page, itemsPerPage, () => [], ngZone, router, activatedRoute);

      expect(ngZone.run).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [] },
      });
    });

    it('should navigate with sort parameters', () => {
      const page = 1;
      const itemsPerPage = 25;
      const predicate = 'name';
      const ascending = true;
      const getSortQueryParam = jest.fn().mockReturnValueOnce(['name,asc']);

      navigateToWithPagingInformation(page, itemsPerPage, getSortQueryParam, ngZone, router, activatedRoute, predicate, ascending);

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

      navigateToWithPagingInformation(page, itemsPerPage, () => [], ngZone, router, activatedRoute, undefined, undefined, filterOptions);

      expect(ngZone.run).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['./'], {
        relativeTo: activatedRoute,
        queryParams: { page, size: itemsPerPage, sort: [], [categoryQueryParam]: ['electronics'], price: ['100-200'] },
      });
    });
  });
});
