import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMessageHeader } from '../message-header.model';
import { MessageHeaderService } from '../service/message-header.service';

export const messageHeaderResolve = (route: ActivatedRouteSnapshot): Observable<null | IMessageHeader> => {
  const headerId = route.params['headerId'];
  if (headerId) {
    return inject(MessageHeaderService)
      .find(headerId)
      .pipe(
        mergeMap((messageHeader: HttpResponse<IMessageHeader>) => {
          if (messageHeader.body) {
            return of(messageHeader.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default messageHeaderResolve;
