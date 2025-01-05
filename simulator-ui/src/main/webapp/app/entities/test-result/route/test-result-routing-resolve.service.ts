import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITestResult } from '../test-result.model';
import { TestResultService } from '../service/test-result.service';

const testResultResolve = (route: ActivatedRouteSnapshot): Observable<null | ITestResult> => {
  const id = route.params.id;
  if (id) {
    return inject(TestResultService)
      .find(id)
      .pipe(
        mergeMap((testResult: HttpResponse<ITestResult>) => {
          if (testResult.body) {
            return of(testResult.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default testResultResolve;
