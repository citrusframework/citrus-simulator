import { EntityOrder, toEntityOrder } from './navigation.constants';

describe('EntityOrder', () => {
  it('ASCENDING compares to "asc"', () => {
    // eslint-disable-next-line
    expect(EntityOrder.ASCENDING === 'asc').toBeTruthy();
  });

  it('DESCENDING compares to "desc"', () => {
    // eslint-disable-next-line
    expect(EntityOrder.DESCENDING === 'desc').toBeTruthy();
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
