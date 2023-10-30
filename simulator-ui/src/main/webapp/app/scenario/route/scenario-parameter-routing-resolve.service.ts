import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';

import { IScenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';
import { IScenarioParameter } from '../../entities/scenario-parameter/scenario-parameter.model';

const scenarioParameterByNameComparator = (a: IScenarioParameter, b: IScenarioParameter): number => a.name!.localeCompare(b.name!);

export const scenarioParameterResolve = (route: ActivatedRouteSnapshot): Observable<null | IScenarioParameter[]> => {
  const name = route.params['name'];
  if (name) {
    return inject(ScenarioService)
      .findParameters(name)
      .pipe(
        mergeMap((scenario: HttpResponse<IScenarioParameter[]>) => {
          if (scenario.body) {
            return of(scenario.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
        map(scenarioParameters => scenarioParameters.sort(scenarioParameterByNameComparator)),
      );
  }
  return of(null);
};

export default scenarioParameterResolve;
