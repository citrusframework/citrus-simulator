import { HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { of, EMPTY, Observable, from } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';

import { IMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import { CodeFormatterService } from 'app/shared/code-formatter.service';

export const messageResolve = (route: ActivatedRouteSnapshot): Observable<null | IMessage> => {
  const messageService = inject(MessageService);
  const codeFormatterService = inject(CodeFormatterService);

  const messageId = route.params['messageId'];

  if (messageId) {
    return messageService.find(messageId).pipe(
      mergeMap((response: HttpResponse<IMessage>) => {
        const message = response.body;

        if (message) {
          if (message.payload) {
            return codeFormatterService.formatCode(message.payload).pipe(
              map(result => {
                message.payload = result;
                return message;
              }),
            );
          } else {
            return of(message);
          }
        } else {
          inject(Router).navigate(['404']);
          return EMPTY;
        }
      }),
    );
  }

  return of(null);
};

export default messageResolve;
