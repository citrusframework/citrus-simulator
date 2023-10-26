import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScenarioAction } from '../scenario-action.model';
import { ScenarioActionService } from '../service/scenario-action.service';

export const scenarioActionResolve = (route: ActivatedRouteSnapshot): Observable<null | IScenarioAction> => {
  const actionId = route.params['actionId'];
  if (actionId) {
    return inject(ScenarioActionService)
      .find(actionId)
      .pipe(
        mergeMap((scenarioAction: HttpResponse<IScenarioAction>) => {
          if (scenarioAction.body) {
            return of(scenarioAction.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default scenarioActionResolve;
