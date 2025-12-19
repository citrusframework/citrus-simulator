import dayjs from 'dayjs/esm';

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
      const sortedMessages = sort(messages, 'id');

      expect(sortedMessages).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }]);
    });

    test('sorts messages by number in descending order', () => {
      const messages = [{ id: 3 }, { id: 1 }, { id: 2 }];
      const sortedMessages = sort(messages, 'id', false);

      expect(sortedMessages).toEqual([{ id: 3 }, { id: 2 }, { id: 1 }]);
    });

    test('sorts messages by string in ascending order', () => {
      const messages = [{ name: 'Charlie' }, { name: 'Alice' }, { name: 'Bob' }];
      const sortedMessages = sort(messages, 'name');

      expect(sortedMessages).toEqual([{ name: 'Alice' }, { name: 'Bob' }, { name: 'Charlie' }]);
    });

    test('sorts messages by string in descending order', () => {
      const messages = [{ name: 'Charlie' }, { name: 'Alice' }, { name: 'Bob' }];
      const sortedMessages = sort(messages, 'name', false);

      expect(sortedMessages).toEqual([{ name: 'Charlie' }, { name: 'Bob' }, { name: 'Alice' }]);
    });

    test('sorts messages by dayjs date in ascending order', () => {
      const messages = [{ timestamp: dayjs('2023-01-03') }, { timestamp: dayjs('2023-01-01') }, { timestamp: dayjs('2023-01-02') }];
      const sortedMessages = sort(messages, 'timestamp');

      expect(sortedMessages).toEqual([
        { timestamp: dayjs('2023-01-01') },
        { timestamp: dayjs('2023-01-02') },
        { timestamp: dayjs('2023-01-03') },
      ]);
    });

    test('sorts messages by dayjs date in descending order', () => {
      const messages = [{ timestamp: dayjs('2023-01-03') }, { timestamp: dayjs('2023-01-01') }, { timestamp: dayjs('2023-01-02') }];
      const sortedMessages = sort(messages, 'timestamp', false);

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
      const sortedMessages = sort(messages, 'id');

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
