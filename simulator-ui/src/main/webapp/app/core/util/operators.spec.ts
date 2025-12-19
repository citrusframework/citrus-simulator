import dayjs from 'dayjs/esm';

import { SortOrder } from 'app/shared/sort';

import { filterNaN, isPresent, sort } from './operators';

describe('Operators Test', () => {
  describe('isPresent', () => {
    it('should remove null and undefined values', () => {
      expect([1, null, undefined].filter(isPresent)).toEqual([1]);
    });
  });

  describe('filterNaN', () => {
    it('should return 0 for NaN', () => {
      expect(filterNaN(NaN)).toBe(0);
    });
    it('should return number for a number', () => {
      expect(filterNaN(12345)).toBe(12345);
    });
  });

  describe('sort', () => {
    test('sorts messages by number in ascending order', () => {
      const messages = [{ id: 3 }, { id: 1 }, { id: 2 }];
      const sortedMessages = sort(messages, { predicate: 'id' }, 'id');

      expect(sortedMessages).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }]);
    });

    test('sorts messages by number in descending order', () => {
      const messages = [{ id: 3 }, { id: 1 }, { id: 2 }];
      const sortedMessages = sort(messages, { predicate: 'id', order: SortOrder.DESCENDING }, 'id');

      expect(sortedMessages).toEqual([{ id: 3 }, { id: 2 }, { id: 1 }]);
    });

    test('sorts messages by string in ascending order', () => {
      const messages = [{ name: 'Charlie' }, { name: 'Alice' }, { name: 'Bob' }];
      const sortedMessages = sort(messages, { predicate: 'name' }, 'name');

      expect(sortedMessages).toEqual([{ name: 'Alice' }, { name: 'Bob' }, { name: 'Charlie' }]);
    });

    test('sorts messages by string in descending order', () => {
      const messages = [{ name: 'Charlie' }, { name: 'Alice' }, { name: 'Bob' }];
      const sortedMessages = sort(messages, { predicate: 'name', order: SortOrder.DESCENDING }, 'name');

      expect(sortedMessages).toEqual([{ name: 'Charlie' }, { name: 'Bob' }, { name: 'Alice' }]);
    });

    test('sorts messages by dayjs date in ascending order', () => {
      const messages = [{ timestamp: dayjs('2023-01-03') }, { timestamp: dayjs('2023-01-01') }, { timestamp: dayjs('2023-01-02') }];
      const sortedMessages = sort(messages, { predicate: 'timestamp' }, 'timestamp');

      expect(sortedMessages).toEqual([
        { timestamp: dayjs('2023-01-01') },
        { timestamp: dayjs('2023-01-02') },
        { timestamp: dayjs('2023-01-03') },
      ]);
    });

    test('sorts messages by dayjs date in descending order', () => {
      const messages = [{ timestamp: dayjs('2023-01-03') }, { timestamp: dayjs('2023-01-01') }, { timestamp: dayjs('2023-01-02') }];
      const sortedMessages = sort(messages, { predicate: 'timestamp', order: SortOrder.DESCENDING }, 'timestamp');

      expect(sortedMessages).toEqual([
        { timestamp: dayjs('2023-01-03') },
        { timestamp: dayjs('2023-01-02') },
        { timestamp: dayjs('2023-01-01') },
      ]);
    });

    test('handles null and undefined values gracefully', () => {
      const messages = [
        { name: 'Alice', id: null },
        { name: null, id: 2 },
        { name: 'Charlie', id: undefined },
        { name: 'Bob', id: 1 },
      ];
      const sortedMessages = sort(messages, { predicate: 'id' }, 'id');

      // Depending on your sort function's implementation to handle null/undefined,
      // adjust the expected result accordingly
      expect(sortedMessages).toEqual([
        { name: 'Bob', id: 1 },
        { name: null, id: 2 },
        { name: 'Alice', id: null },
        { name: 'Charlie', id: undefined },
      ]);
    });
  });
});
