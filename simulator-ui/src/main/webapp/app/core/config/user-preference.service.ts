import { Injectable } from '@angular/core';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

@Injectable({ providedIn: 'root' })
export class UserPreferenceService {
  private paginationPrefix = 'psize';

  public getPreferredPageSize(key: string): number {
    return Number(localStorage.getItem(this.paginationId(key)) ?? ITEMS_PER_PAGE);
  }

  public setPreferredPageSize(key: string, size: number): void {
    localStorage.setItem(this.paginationId(key), size.toString());
  }

  private paginationId(key: string): string {
    return `${this.paginationPrefix}-${key}`;
  }
}
