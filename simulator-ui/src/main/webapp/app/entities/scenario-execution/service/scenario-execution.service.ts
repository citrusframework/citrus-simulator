import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IScenarioExecution } from '../scenario-execution.model';

type RestOf<T extends IScenarioExecution> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestScenarioExecution = RestOf<IScenarioExecution>;

export type EntityResponseType = HttpResponse<IScenarioExecution>;
export type EntityArrayResponseType = HttpResponse<IScenarioExecution[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioExecutionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scenario-executions');

  find(executionId: number): Observable<EntityResponseType> {
    return this.http
      .get<RestScenarioExecution>(`${this.resourceUrl}/${executionId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestScenarioExecution[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getScenarioExecutionIdentifier(scenarioExecution: Pick<IScenarioExecution, 'executionId'>): number {
    return scenarioExecution.executionId;
  }

  compareScenarioExecution(
    o1: Pick<IScenarioExecution, 'executionId'> | null,
    o2: Pick<IScenarioExecution, 'executionId'> | null,
  ): boolean {
    return o1 && o2 ? this.getScenarioExecutionIdentifier(o1) === this.getScenarioExecutionIdentifier(o2) : o1 === o2;
  }

  addScenarioExecutionToCollectionIfMissing<Type extends Pick<IScenarioExecution, 'executionId'>>(
    scenarioExecutionCollection: Type[],
    ...scenarioExecutionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scenarioExecutions: Type[] = scenarioExecutionsToCheck.filter(isPresent);
    if (scenarioExecutions.length > 0) {
      const scenarioExecutionCollectionIdentifiers = scenarioExecutionCollection.map(scenarioExecutionItem =>
        this.getScenarioExecutionIdentifier(scenarioExecutionItem),
      );
      const scenarioExecutionsToAdd = scenarioExecutions.filter(scenarioExecutionItem => {
        const scenarioExecutionIdentifier = this.getScenarioExecutionIdentifier(scenarioExecutionItem);
        if (scenarioExecutionCollectionIdentifiers.includes(scenarioExecutionIdentifier)) {
          return false;
        }
        scenarioExecutionCollectionIdentifiers.push(scenarioExecutionIdentifier);
        return true;
      });
      return [...scenarioExecutionsToAdd, ...scenarioExecutionCollection];
    }
    return scenarioExecutionCollection;
  }

  protected convertDateFromClient<T extends IScenarioExecution>(scenarioExecution: T): RestOf<T> {
    return {
      ...scenarioExecution,
      startDate: scenarioExecution.startDate?.toJSON() ?? null,
      endDate: scenarioExecution.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restScenarioExecution: RestScenarioExecution): IScenarioExecution {
    return {
      ...restScenarioExecution,
      startDate: restScenarioExecution.startDate ? dayjs(restScenarioExecution.startDate) : undefined,
      endDate: restScenarioExecution.endDate ? dayjs(restScenarioExecution.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestScenarioExecution>): HttpResponse<IScenarioExecution> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestScenarioExecution[]>): HttpResponse<IScenarioExecution[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
