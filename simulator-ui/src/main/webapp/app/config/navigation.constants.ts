export const SORT = 'sort';
export const ITEM_DELETED_EVENT = 'deleted';
export const DEFAULT_SORT_DATA = 'defaultSort';

export enum EntityOrder {
  ASCENDING = 'asc',
  DESCENDING = 'desc',
}

export const toEntityOrder = (value: string): EntityOrder | undefined => {
  if (value.toLowerCase() === EntityOrder.ASCENDING.toLowerCase()) {
    return EntityOrder.ASCENDING;
  } else if (value.toLowerCase() === EntityOrder.DESCENDING.toLowerCase()) {
    return EntityOrder.DESCENDING;
  }

  return undefined;
};
