import { Injectable, WritableSignal } from '@angular/core';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { SortOrder, SortState, sortStateSignal, toSortOrder } from 'app/shared/sort';

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

  public getSortState(identifier: string, defaultPredicate: string): WritableSignal<SortState> {
    const predicate = this.getPredicate(identifier, defaultPredicate);
    const order = this.getSortOrder(identifier);
    return sortStateSignal({ predicate, order });
  }

  public setSortState(identifier: string, sortState: SortState): void {
    if (sortState.predicate) {
      this.setPredicate(identifier, sortState.predicate);
    }
    if (sortState.order) {
      this.setSortOrder(identifier, sortState.order);
    }
  }

  private getPredicate(identifier: string, defaultPredicate: string): string {
    return localStorage.getItem(this.predicateId(identifier)) ?? defaultPredicate;
  }

  private setPredicate(identifier: string, predicate: string): void {
    localStorage.setItem(this.predicateId(identifier), predicate);
  }

  private getSortOrder(identifier: string): SortOrder {
    return toSortOrder(localStorage.getItem(this.orderId(identifier)) ?? SortOrder.ASCENDING) ?? SortOrder.ASCENDING;
  }

  private setSortOrder(identifier: string, entityOrder: SortOrder): void {
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
