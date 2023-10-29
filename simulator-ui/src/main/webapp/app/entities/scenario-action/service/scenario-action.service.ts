import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IScenarioAction, NewScenarioAction } from '../scenario-action.model';

type RestOf<T extends IScenarioAction | NewScenarioAction> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestScenarioAction = RestOf<IScenarioAction>;

export type EntityResponseType = HttpResponse<IScenarioAction>;
export type EntityArrayResponseType = HttpResponse<IScenarioAction[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioActionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scenario-actions');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  find(actionId: number): Observable<EntityResponseType> {
    return this.http
      .get<RestScenarioAction>(`${this.resourceUrl}/${actionId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestScenarioAction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getScenarioActionIdentifier(scenarioAction: Pick<IScenarioAction, 'actionId'>): number {
    return scenarioAction.actionId;
  }

  compareScenarioAction(o1: Pick<IScenarioAction, 'actionId'> | null, o2: Pick<IScenarioAction, 'actionId'> | null): boolean {
    return o1 && o2 ? this.getScenarioActionIdentifier(o1) === this.getScenarioActionIdentifier(o2) : o1 === o2;
  }

  addScenarioActionToCollectionIfMissing<Type extends Pick<IScenarioAction, 'actionId'>>(
    scenarioActionCollection: Type[],
    ...scenarioActionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scenarioActions: Type[] = scenarioActionsToCheck.filter(isPresent);
    if (scenarioActions.length > 0) {
      const scenarioActionCollectionIdentifiers = scenarioActionCollection.map(
        scenarioActionItem => this.getScenarioActionIdentifier(scenarioActionItem)!,
      );
      const scenarioActionsToAdd = scenarioActions.filter(scenarioActionItem => {
        const scenarioActionIdentifier = this.getScenarioActionIdentifier(scenarioActionItem);
        if (scenarioActionCollectionIdentifiers.includes(scenarioActionIdentifier)) {
          return false;
        }
        scenarioActionCollectionIdentifiers.push(scenarioActionIdentifier);
        return true;
      });
      return [...scenarioActionsToAdd, ...scenarioActionCollection];
    }
    return scenarioActionCollection;
  }

  protected convertDateFromClient<T extends IScenarioAction | NewScenarioAction>(scenarioAction: T): RestOf<T> {
    return {
      ...scenarioAction,
      startDate: scenarioAction.startDate?.toJSON() ?? null,
      endDate: scenarioAction.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restScenarioAction: RestScenarioAction): IScenarioAction {
    return {
      ...restScenarioAction,
      startDate: restScenarioAction.startDate ? dayjs(restScenarioAction.startDate) : undefined,
      endDate: restScenarioAction.endDate ? dayjs(restScenarioAction.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestScenarioAction>): HttpResponse<IScenarioAction> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestScenarioAction[]>): HttpResponse<IScenarioAction[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
