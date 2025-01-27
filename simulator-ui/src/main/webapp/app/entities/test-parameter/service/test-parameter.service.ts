import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITestParameter } from '../test-parameter.model';

type RestOf<T extends ITestParameter> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestTestParameter = RestOf<ITestParameter>;

export type EntityResponseType = HttpResponse<ITestParameter>;
export type EntityArrayResponseType = HttpResponse<ITestParameter[]>;

@Injectable({ providedIn: 'root' })
export class TestParameterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/test-parameters');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  find(testResultId: number, key: string): Observable<EntityResponseType> {
    return this.http
      .get<RestTestParameter>(`${this.resourceUrl}/${testResultId}/${key}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTestParameter[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getTestParameterIdentifier(testParameter: Pick<ITestParameter, 'key' | 'testResult'>): number {
    return this.hash((testParameter.testResult.id ? testParameter.testResult.id : 0).toString() + '-' + testParameter.key!);
  }

  compareTestParameter(
    o1: Pick<ITestParameter, 'key' | 'testResult'> | null,
    o2: Pick<ITestParameter, 'key' | 'testResult'> | null,
  ): boolean {
    return o1 && o2 ? this.getTestParameterIdentifier(o1) === this.getTestParameterIdentifier(o2) : o1 === o2;
  }

  addTestParameterToCollectionIfMissing<Type extends Pick<ITestParameter, 'key' | 'testResult'>>(
    testParameterCollection: Type[],
    ...testParametersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const testParameters: Type[] = testParametersToCheck.filter(isPresent);
    if (testParameters.length > 0) {
      const testParameterCollectionIdentifiers = testParameterCollection.map(testParameterItem =>
        this.getTestParameterIdentifier(testParameterItem),
      );
      const testParametersToAdd = testParameters.filter(testParameterItem => {
        const testParameterIdentifier = this.getTestParameterIdentifier(testParameterItem);
        if (testParameterCollectionIdentifiers.includes(testParameterIdentifier)) {
          return false;
        }
        testParameterCollectionIdentifiers.push(testParameterIdentifier);
        return true;
      });
      return [...testParametersToAdd, ...testParameterCollection];
    }
    return testParameterCollection;
  }

  protected convertDateFromServer(restTestParameter: RestTestParameter): ITestParameter {
    return {
      ...restTestParameter,
      createdDate: restTestParameter.createdDate ? dayjs(restTestParameter.createdDate) : undefined,
      lastModifiedDate: restTestParameter.lastModifiedDate ? dayjs(restTestParameter.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTestParameter>): HttpResponse<ITestParameter> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTestParameter[]>): HttpResponse<ITestParameter[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }

  /**
   * Compute a "java-like" hash from the given string. Required because we cannot simply return a numerical identifier
   * for the {@link ITestParameter} type: It uses a composed primary key.
   *
   * @param composedKey The composed key: `{@link ITestParameter#testResult#id} + '-' + {@link ITestResult#key}`
   * @private
   */
  private hash(composedKey: string): number {
    let hash = 0;
    if (composedKey.length === 0) {
      return hash;
    }
    for (let i = 0; i < composedKey.length; i++) {
      const char = composedKey.charCodeAt(i);
      // eslint-disable-next-line no-bitwise
      hash = (hash << 5) - hash + char;
      // eslint-disable-next-line no-bitwise
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  }
}
