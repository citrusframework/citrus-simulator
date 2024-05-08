import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { MessageHeaderService, RestMessageHeader } from 'app/entities/message-header/service/message-header.service';

import { IMessage } from '../message.model';

type RestOf<T extends IMessage> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestMessage = RestOf<IMessage>;

export type EntityResponseType = HttpResponse<IMessage>;
export type EntityArrayResponseType = HttpResponse<IMessage[]>;

@Injectable({ providedIn: 'root' })
export class MessageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/messages');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private messageHeaderService: MessageHeaderService,
  ) {}

  find(messageId: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMessage>(`${this.resourceUrl}/${messageId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMessage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getMessageIdentifier(message: Pick<IMessage, 'messageId'>): number {
    return message.messageId;
  }

  compareMessage(o1: Pick<IMessage, 'messageId'> | null, o2: Pick<IMessage, 'messageId'> | null): boolean {
    return o1 && o2 ? this.getMessageIdentifier(o1) === this.getMessageIdentifier(o2) : o1 === o2;
  }

  addMessageToCollectionIfMissing<Type extends Pick<IMessage, 'messageId'>>(
    messageCollection: Type[],
    ...messagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const messages: Type[] = messagesToCheck.filter(isPresent);
    if (messages.length > 0) {
      const messageCollectionIdentifiers = messageCollection.map(messageItem => this.getMessageIdentifier(messageItem));
      const messagesToAdd = messages.filter(messageItem => {
        const messageIdentifier = this.getMessageIdentifier(messageItem);
        if (messageCollectionIdentifiers.includes(messageIdentifier)) {
          return false;
        }
        messageCollectionIdentifiers.push(messageIdentifier);
        return true;
      });
      return [...messagesToAdd, ...messageCollection];
    }
    return messageCollection;
  }

  protected convertDateFromServer(restMessage: RestMessage): IMessage {
    return {
      ...restMessage,
      createdDate: restMessage.createdDate ? dayjs(restMessage.createdDate) : undefined,
      lastModifiedDate: restMessage.lastModifiedDate ? dayjs(restMessage.lastModifiedDate) : undefined,
      headers: restMessage.headers
        ? restMessage.headers.map(messageHeader => messageHeader as RestMessageHeader).map(this.messageHeaderService.convertDateFromServer)
        : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMessage>): HttpResponse<IMessage> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMessage[]>): HttpResponse<IMessage[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
