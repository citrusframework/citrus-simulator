export const ASC = 'asc';
export const DESC = 'desc';
export const SORT = 'sort';
export const ITEM_DELETED_EVENT = 'deleted';
export const DEFAULT_SORT_DATA = 'defaultSort';

export enum EntityOrder {
  ASCENDING = ASC,
  DESCENDING = DESC,
}

export const toEntityOrder = (value: string): EntityOrder | undefined => {
  if (value.toUpperCase() === 'ASC') {
    return EntityOrder.ASCENDING;
  } else if (value.toUpperCase() === 'DESC') {
    return EntityOrder.DESCENDING;
  }

  return undefined;
};
