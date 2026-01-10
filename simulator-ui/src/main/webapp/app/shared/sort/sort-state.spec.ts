import { SortOrder, toSortOrder } from './sort-state';

describe('SortOrder', () => {
  it('ASCENDING compares to "asc"', () => {
    expect(SortOrder.ASCENDING.toString() === 'asc').toBeTruthy();
  });

  it('DESCENDING compares to "desc"', () => {
    expect(SortOrder.DESCENDING.toString() === 'desc').toBeTruthy();
  });

  describe('toSortOrder', () => {
    it('returns ASCENDING for "ASC"', () => {
      expect(toSortOrder('ASC')).toEqual(SortOrder.ASCENDING);
    });
    it('returns ASCENDING for "asc"', () => {
      expect(toSortOrder(SortOrder.ASCENDING)).toEqual(SortOrder.ASCENDING);
    });

    it('returns DESCENDING for "DESC"', () => {
      expect(toSortOrder('DESC')).toEqual(SortOrder.DESCENDING);
    });

    it('returns DESCENDING for "desc"', () => {
      expect(toSortOrder(SortOrder.DESCENDING)).toEqual(SortOrder.DESCENDING);
    });

    it('returns undefined for invalid input', () => {
      expect(toSortOrder('invalid')).toBeUndefined();
    });
  });
});
