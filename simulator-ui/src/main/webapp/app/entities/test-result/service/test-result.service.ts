import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITestResult } from '../test-result.model';

type RestOf<T extends ITestResult> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestTestResult = RestOf<ITestResult>;

export type EntityResponseType = HttpResponse<ITestResult>;
export type EntityArrayResponseType = HttpResponse<ITestResult[]>;

export type TestResultsByStatus = {
  total: number;
  successful: number;
  failed: number;
};

@Injectable({ providedIn: 'root' })
export class TestResultService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/test-results');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTestResult>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTestResult[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  countByStatus(): Observable<HttpResponse<TestResultsByStatus>> {
    return this.http.get<TestResultsByStatus>(`${this.resourceUrl}/count-by-status`, { observe: 'response' });
  }

  deleteAll(): Observable<HttpResponse<void>> {
    // eslint-disable-next-line @typescript-eslint/no-invalid-void-type
    return this.http.delete<void>(this.resourceUrl, { observe: 'response' });
  }

  getTestResultIdentifier(testResult: Pick<ITestResult, 'id'>): number {
    return testResult.id;
  }

  compareTestResult(o1: Pick<ITestResult, 'id'> | null, o2: Pick<ITestResult, 'id'> | null): boolean {
    return o1 && o2 ? this.getTestResultIdentifier(o1) === this.getTestResultIdentifier(o2) : o1 === o2;
  }

  addTestResultToCollectionIfMissing<Type extends Pick<ITestResult, 'id'>>(
    testResultCollection: Type[],
    ...testResultsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const testResults: Type[] = testResultsToCheck.filter(isPresent);
    if (testResults.length > 0) {
      const testResultCollectionIdentifiers = testResultCollection.map(testResultItem => this.getTestResultIdentifier(testResultItem));
      const testResultsToAdd = testResults.filter(testResultItem => {
        const testResultIdentifier = this.getTestResultIdentifier(testResultItem);
        if (testResultCollectionIdentifiers.includes(testResultIdentifier)) {
          return false;
        }
        testResultCollectionIdentifiers.push(testResultIdentifier);
        return true;
      });
      return [...testResultsToAdd, ...testResultCollection];
    }
    return testResultCollection;
  }

  protected convertDateFromServer(restTestResult: RestTestResult): ITestResult {
    return {
      ...restTestResult,
      createdDate: restTestResult.createdDate ? dayjs(restTestResult.createdDate) : undefined,
      lastModifiedDate: restTestResult.lastModifiedDate ? dayjs(restTestResult.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTestResult>): HttpResponse<ITestResult> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTestResult[]>): HttpResponse<ITestResult[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
