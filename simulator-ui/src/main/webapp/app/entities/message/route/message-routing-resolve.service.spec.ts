import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMessage } from '../message.model';
import { MessageService } from '../service/message.service';

import messageResolve from './message-routing-resolve.service';

describe('Message routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: MessageService;
  let resultMessage: IMessage | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
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
    service = TestBed.inject(MessageService);
    resultMessage = undefined;
  });

  describe('resolve', () => {
    it('should return IMessage returned by find', () => {
      // GIVEN
      service.find = jest.fn(messageId => of(new HttpResponse({ body: { messageId } })));
      mockActivatedRouteSnapshot.params = { messageId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessage = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultMessage).toEqual({ messageId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessage = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultMessage).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IMessage>({ body: null })));
      mockActivatedRouteSnapshot.params = { messageId: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessage = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultMessage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
