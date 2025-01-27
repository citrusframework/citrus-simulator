import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMessageHeader } from '../message-header.model';
import { MessageHeaderService } from '../service/message-header.service';

import messageHeaderResolve from './message-header-routing-resolve.service';

describe('MessageHeader routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: MessageHeaderService;
  let resultMessageHeader: IMessageHeader | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting(), provideRouter([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(MessageHeaderService);
    resultMessageHeader = undefined;
  });

  describe('resolve', () => {
    it('should return IMessageHeader returned by find', () => {
      // GIVEN
      service.find = jest.fn(headerId => of(new HttpResponse({ body: { headerId } })));
      mockActivatedRouteSnapshot.params = { headerId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageHeaderResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessageHeader = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultMessageHeader).toEqual({ headerId: 123 });
    });

    it('should return null if headerId is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageHeaderResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessageHeader = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultMessageHeader).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IMessageHeader>({ body: null })));
      mockActivatedRouteSnapshot.params = { headerId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageHeaderResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessageHeader = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultMessageHeader).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
