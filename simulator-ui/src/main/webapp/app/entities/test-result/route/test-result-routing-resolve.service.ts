import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';

export const testResultResolve = (route: ActivatedRouteSnapshot): Observable<null | ITestResult> => {
  const id = route.params['id'];
  if (id) {
    return inject(TestResultService)
      .find(id)
      .pipe(
        mergeMap((testResult: HttpResponse<ITestResult>) => {
          if (testResult.body) {
            return of(testResult.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default testResultResolve;
