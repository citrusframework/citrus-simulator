import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScenarioExecution } from '../scenario-execution.model';
import { ScenarioExecutionService } from '../service/scenario-execution.service';

export const scenarioExecutionResolve = (route: ActivatedRouteSnapshot): Observable<null | IScenarioExecution> => {
  const executionId = route.params['executionId'];
  if (executionId) {
    return inject(ScenarioExecutionService)
      .find(executionId)
      .pipe(
        mergeMap((scenarioExecution: HttpResponse<IScenarioExecution>) => {
          if (scenarioExecution.body) {
            return of(scenarioExecution.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default scenarioExecutionResolve;
