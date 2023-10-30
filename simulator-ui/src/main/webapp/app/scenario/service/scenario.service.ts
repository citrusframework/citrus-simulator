import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';

import { IScenario } from '../scenario.model';
import { IScenarioParameter } from '../../entities/scenario-parameter/scenario-parameter.model';

export type RestScenario = IScenario;

export type EntityResponseType = HttpResponse<IScenario>;
export type EntityArrayResponseType = HttpResponse<IScenario[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scenarios');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  findParameters(name: string): Observable<HttpResponse<IScenarioParameter[]>> {
    return this.http.get<IScenarioParameter[]>(`${this.resourceUrl}/${name}/parameters`, { observe: 'response' });
  }

  launch(name: string, parameters: IScenarioParameter[] = []): Observable<HttpResponse<number>> {
    return this.http.post<number>(`${this.resourceUrl}/${name}/launch`, parameters, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestScenario[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getScenarioIdentifier(scenario: Pick<IScenario, 'name'>): string {
    return scenario.name;
  }

  compareScenario(o1: Pick<IScenario, 'name'> | null, o2: Pick<IScenario, 'name'> | null): boolean {
    return o1 && o2 ? this.getScenarioIdentifier(o1) === this.getScenarioIdentifier(o2) : o1 === o2;
  }

  addScenarioToCollectionIfMissing<Type extends Pick<IScenario, 'name'>>(
    scenarioCollection: Type[],
    ...scenariosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scenarios: Type[] = scenariosToCheck.filter(isPresent);
    if (scenarios.length > 0) {
      const scenarioCollectionIdentifiers = scenarioCollection.map(scenarioItem => this.getScenarioIdentifier(scenarioItem)!);
      const scenariosToAdd = scenarios.filter(scenarioItem => {
        const scenarioIdentifier = this.getScenarioIdentifier(scenarioItem);
        if (scenarioCollectionIdentifiers.includes(scenarioIdentifier)) {
          return false;
        }
        scenarioCollectionIdentifiers.push(scenarioIdentifier);
        return true;
      });
      return [...scenariosToAdd, ...scenarioCollection];
    }
    return scenarioCollection;
  }
}
