import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';

import { IMessageHeader, NewMessageHeader } from '../message-header.model';

type RestOf<T extends IMessageHeader | NewMessageHeader> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestMessageHeader = RestOf<IMessageHeader>;

export type EntityResponseType = HttpResponse<IMessageHeader>;
export type EntityArrayResponseType = HttpResponse<IMessageHeader[]>;

@Injectable({ providedIn: 'root' })
export class MessageHeaderService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/message-headers');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  find(headerId: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMessageHeader>(`${this.resourceUrl}/${headerId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMessageHeader[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getMessageHeaderIdentifier(messageHeader: Pick<IMessageHeader, 'headerId'>): number {
    return messageHeader.headerId;
  }

  compareMessageHeader(o1: Pick<IMessageHeader, 'headerId'> | null, o2: Pick<IMessageHeader, 'headerId'> | null): boolean {
    return o1 && o2 ? this.getMessageHeaderIdentifier(o1) === this.getMessageHeaderIdentifier(o2) : o1 === o2;
  }

  addMessageHeaderToCollectionIfMissing<Type extends Pick<IMessageHeader, 'headerId'>>(
    messageHeaderCollection: Type[],
    ...messageHeadersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const messageHeaders: Type[] = messageHeadersToCheck.filter(isPresent);
    if (messageHeaders.length > 0) {
      const messageHeaderCollectionIdentifiers = messageHeaderCollection.map(
        messageHeaderItem => this.getMessageHeaderIdentifier(messageHeaderItem)!,
      );
      const messageHeadersToAdd = messageHeaders.filter(messageHeaderItem => {
        const messageHeaderIdentifier = this.getMessageHeaderIdentifier(messageHeaderItem);
        if (messageHeaderCollectionIdentifiers.includes(messageHeaderIdentifier)) {
          return false;
        }
        messageHeaderCollectionIdentifiers.push(messageHeaderIdentifier);
        return true;
      });
      return [...messageHeadersToAdd, ...messageHeaderCollection];
    }
    return messageHeaderCollection;
  }

  protected convertDateFromServer(restMessageHeader: RestMessageHeader): IMessageHeader {
    return {
      ...restMessageHeader,
      createdDate: restMessageHeader.createdDate ? dayjs(restMessageHeader.createdDate) : undefined,
      lastModifiedDate: restMessageHeader.lastModifiedDate ? dayjs(restMessageHeader.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMessageHeader>): HttpResponse<IMessageHeader> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMessageHeader[]>): HttpResponse<IMessageHeader[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
