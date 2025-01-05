import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IScenarioParameter } from '../scenario-parameter.model';

type RestOf<T extends IScenarioParameter> = Omit<T, 'lastModifiedDate'> & {
  lastModifiedDate?: string | null;
};

export type RestScenarioParameter = RestOf<IScenarioParameter>;

export type EntityResponseType = HttpResponse<IScenarioParameter>;
export type EntityArrayResponseType = HttpResponse<IScenarioParameter[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioParameterService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scenario-parameters');

  find(parameterId: number): Observable<EntityResponseType> {
    return this.http
      .get<RestScenarioParameter>(`${this.resourceUrl}/${parameterId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestScenarioParameter[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getScenarioParameterIdentifier(scenarioParameter: Pick<IScenarioParameter, 'parameterId'>): number {
    return scenarioParameter.parameterId;
  }

  compareScenarioParameter(
    o1: Pick<IScenarioParameter, 'parameterId'> | null,
    o2: Pick<IScenarioParameter, 'parameterId'> | null,
  ): boolean {
    return o1 && o2 ? this.getScenarioParameterIdentifier(o1) === this.getScenarioParameterIdentifier(o2) : o1 === o2;
  }

  addScenarioParameterToCollectionIfMissing<Type extends Pick<IScenarioParameter, 'parameterId'>>(
    scenarioParameterCollection: Type[],
    ...scenarioParametersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scenarioParameters: Type[] = scenarioParametersToCheck.filter(isPresent);
    if (scenarioParameters.length > 0) {
      const scenarioParameterCollectionIdentifiers = scenarioParameterCollection.map(scenarioParameterItem =>
        this.getScenarioParameterIdentifier(scenarioParameterItem),
      );
      const scenarioParametersToAdd = scenarioParameters.filter(scenarioParameterItem => {
        const scenarioParameterIdentifier = this.getScenarioParameterIdentifier(scenarioParameterItem);
        if (scenarioParameterCollectionIdentifiers.includes(scenarioParameterIdentifier)) {
          return false;
        }
        scenarioParameterCollectionIdentifiers.push(scenarioParameterIdentifier);
        return true;
      });
      return [...scenarioParametersToAdd, ...scenarioParameterCollection];
    }
    return scenarioParameterCollection;
  }

  protected convertDateFromClient<T extends IScenarioParameter>(scenarioParameter: T): RestOf<T> {
    return {
      ...scenarioParameter,
      lastModifiedDate: scenarioParameter.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restScenarioParameter: RestScenarioParameter): IScenarioParameter {
    return {
      ...restScenarioParameter,
      lastModifiedDate: restScenarioParameter.lastModifiedDate ? dayjs(restScenarioParameter.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestScenarioParameter>): HttpResponse<IScenarioParameter> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestScenarioParameter[]>): HttpResponse<IScenarioParameter[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
