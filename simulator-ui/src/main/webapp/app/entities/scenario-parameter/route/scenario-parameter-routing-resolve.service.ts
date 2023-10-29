import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScenarioParameter } from '../scenario-parameter.model';
import { ScenarioParameterService } from '../service/scenario-parameter.service';

export const scenarioParameterResolve = (route: ActivatedRouteSnapshot): Observable<null | IScenarioParameter> => {
  const parameterId = route.params['parameterId'];
  if (parameterId) {
    return inject(ScenarioParameterService)
      .find(parameterId)
      .pipe(
        mergeMap((scenarioParameter: HttpResponse<IScenarioParameter>) => {
          if (scenarioParameter.body) {
            return of(scenarioParameter.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default scenarioParameterResolve;
