import { Injectable } from '@angular/core';

import { EntityOrder, toEntityOrder } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

@Injectable({ providedIn: 'root' })
export class UserPreferenceService {
  private paginationPrefix = 'psize';
  private predicatePrefix = 'predicate';
  private orderPrefix = 'order';

  public getPageSize(identifier: string): number {
    return Number(localStorage.getItem(this.paginationId(identifier)) ?? ITEMS_PER_PAGE);
  }

  public setPageSize(identifier: string, size: number): void {
    localStorage.setItem(this.paginationId(identifier), size.toString());
  }

  public getPredicate(identifier: string, defaultValue: string): string {
    return localStorage.getItem(this.predicateId(identifier)) ?? defaultValue;
  }

  public setPredicate(identifier: string, predicate: string): void {
    localStorage.setItem(this.predicateId(identifier), predicate);
  }

  public getEntityOrder(identifier: string): EntityOrder {
    return toEntityOrder(localStorage.getItem(this.orderId(identifier)) ?? EntityOrder.ASCENDING) ?? EntityOrder.ASCENDING;
  }

  public setEntityOrder(identifier: string, entityOrder: EntityOrder): void {
    localStorage.setItem(this.orderId(identifier), entityOrder.toString());
  }

  private paginationId(identifier: string): string {
    return `${this.paginationPrefix}-${identifier}`;
  }

  private predicateId(identifier: string): string {
    return `${this.predicatePrefix}-${identifier}`;
  }

  private orderId(identifier: string): string {
    return `${this.orderPrefix}-${identifier}`;
  }
}
