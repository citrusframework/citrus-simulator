import { ASC, DESC, EntityOrder, toEntityOrder } from './navigation.constants';

describe('EntityOrder', () => {
  it('ASCENDING compares to "ASC"', () => {
    expect(EntityOrder.ASCENDING.toString() === ASC).toBeTruthy();
  });

  it('DESCENDING compares to "DESC"', () => {
    expect(EntityOrder.DESCENDING.toString() === DESC).toBeTruthy();
  });

  describe('toEntityOrder', () => {
    it('returns ASCENDING for "ASC"', () => {
      expect(toEntityOrder('ASC')).toEqual(EntityOrder.ASCENDING);
    });
    it('returns ASCENDING for "asc"', () => {
      expect(toEntityOrder('asc')).toEqual(EntityOrder.ASCENDING);
    });

    it('returns DESCENDING for "DESC"', () => {
      expect(toEntityOrder('DESC')).toEqual(EntityOrder.DESCENDING);
    });

    it('returns DESCENDING for "desc"', () => {
      expect(toEntityOrder('desc')).toEqual(EntityOrder.DESCENDING);
    });

    it('returns undefined for invalid input', () => {
      expect(toEntityOrder('invalid')).toBeUndefined();
    });
  });
});
