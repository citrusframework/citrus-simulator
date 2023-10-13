import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
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
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
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
      expect(service.find).toBeCalledWith(123);
      expect(resultMessage).toEqual({ messageId: 123 });
    });

    it('should return null if messageId is not provided', () => {
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
      expect(service.find).not.toBeCalled();
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
      expect(service.find).toBeCalledWith(123);
      expect(resultMessage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
