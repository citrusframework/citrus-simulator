import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CodeFormatterService } from 'app/shared/code-formatter.service';

import { IMessage } from '../message.model';
import { MessageService } from '../service/message.service';

import messageResolve from './message-routing-resolve.service';

describe('Message routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;

  let codeFormatterService: CodeFormatterService;
  let messageService: MessageService;

  let resultMessage: IMessage | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
        {
          provide: CodeFormatterService,
          useValue: {
            formatCode: jest.fn(),
          },
        },
      ],
    });

    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));

    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;

    codeFormatterService = TestBed.inject(CodeFormatterService);
    messageService = TestBed.inject(MessageService);

    resultMessage = undefined;
  });

  describe('resolve', () => {
    it('should return IMessage returned by find', () => {
      // GIVEN
      messageService.find = jest.fn((messageId: number) => of(new HttpResponse<IMessage>({ body: { messageId } })));
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
      expect(messageService.find).toHaveBeenCalledWith(123);
      expect(codeFormatterService.formatCode).not.toHaveBeenCalled();
      expect(resultMessage).toEqual({ messageId: 123 });
    });

    it('should return null if messageId is not provided', () => {
      // GIVEN
      messageService.find = jest.fn();
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
      expect(messageService.find).not.toHaveBeenCalled();
      expect(resultMessage).toEqual(null);
    });

    it('should return IMessage returned by find with formatted payload', () => {
      // GIVEN
      messageService.find = jest.fn((messageId: number) => of(new HttpResponse<IMessage>({ body: { messageId, payload: 'foo' } })));
      mockActivatedRouteSnapshot.params = { messageId: 123 };
      (codeFormatterService.formatCode as jest.Mock).mockReturnValueOnce(of('bar'));

      // WHEN
      TestBed.runInInjectionContext(() => {
        messageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultMessage = result;
          },
        });
      });

      // THEN
      expect(messageService.find).toHaveBeenCalledWith(123);
      expect(codeFormatterService.formatCode).toHaveBeenCalledWith('foo');
      expect(resultMessage).toEqual({ messageId: 123, payload: 'bar' });
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(messageService, 'find').mockReturnValue(of(new HttpResponse<IMessage>({ body: null })));
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
      expect(messageService.find).toHaveBeenCalledWith(123);
      expect(resultMessage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
