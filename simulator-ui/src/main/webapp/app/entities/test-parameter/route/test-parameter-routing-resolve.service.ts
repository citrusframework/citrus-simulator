import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITestParameter } from '../test-parameter.model';
import { TestParameterService } from '../service/test-parameter.service';

export const testParameterResolve = (route: ActivatedRouteSnapshot): Observable<null | ITestParameter> => {
  const testResultId = route.params['testResultId'];
  const key = route.params['key'];

  if (key && testResultId) {
    return inject(TestParameterService)
      .find(testResultId, key)
      .pipe(
        mergeMap((testParameter: HttpResponse<ITestParameter>) => {
          if (testParameter.body) {
            return of(testParameter.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default testParameterResolve;
