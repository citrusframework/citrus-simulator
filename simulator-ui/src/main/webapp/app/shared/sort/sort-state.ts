import { WritableSignal, signal } from '@angular/core';

export enum SortOrder {
  ASCENDING = 'asc',
  DESCENDING = 'desc',
}

export const toSortOrder = (value: string): SortOrder | undefined => {
  if (value.toUpperCase() === 'ASC') {
    return SortOrder.ASCENDING;
  } else if (value.toUpperCase() === 'DESC') {
    return SortOrder.DESCENDING;
  }

  return undefined;
};

export type SortState = { predicate?: string; order?: SortOrder };

export const sortStateSignal = (state: SortState): WritableSignal<SortState> =>
  signal<SortState>(state, {
    equal: (a, b) => a.predicate === b.predicate && a.order === b.order,
  });
