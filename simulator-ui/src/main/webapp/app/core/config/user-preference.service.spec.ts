import { TestBed } from '@angular/core/testing';

import { SortOrder, sortStateSignal } from 'app/shared/sort';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';

import { UserPreferenceService } from './user-preference.service';

type Mock = jest.Mock;

describe('UserPreferenceService', () => {
  let service: UserPreferenceService;
  let mockLocalStorage: Partial<Storage> & {
    setItem: Mock;
    getItem: Mock;
    removeItem: Mock;
  };

  beforeEach(() => {
    mockLocalStorage = {
      setItem: jest.fn(),
      getItem: jest.fn(),
      removeItem: jest.fn(),
    };

    Object.defineProperty(window, 'localStorage', { value: mockLocalStorage, writable: true });

    TestBed.configureTestingModule({
      providers: [UserPreferenceService],
    });

    service = TestBed.inject(UserPreferenceService);
  });

  describe('setPageSize', () => {
    it('should return known item from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce('1234');

      const preferredPageSize = service.getPageSize('key');

      expect(preferredPageSize).toEqual(1234);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('psize-key');
    });

    it('should return default page size if item is not in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null);

      const preferredPageSize = service.getPageSize('key');

      expect(preferredPageSize).toEqual(ITEMS_PER_PAGE);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('psize-key');
    });
  });

  describe('setPageSize', () => {
    it('should save item in localStorage', () => {
      service.setPageSize('key', 1234);

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('psize-key', '1234');
    });
  });

  describe('getSortState', () => {
    it('should return predicate from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce('somePredicate').mockReturnValueOnce(null);
      const state = service.getSortState('key', 'defaultPredicate');
      expect(state().predicate).toEqual('somePredicate');
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('predicate-key');
    });

    it('should return default predicate if not in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null).mockReturnValueOnce(null);
      const state = service.getSortState('key', 'defaultPredicate');
      expect(state().predicate).toEqual('defaultPredicate');
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('predicate-key');
    });

    it('should return sort order from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null).mockReturnValueOnce('DESC');
      const state = service.getSortState('key', 'defaultPredicate');
      expect(state().order).toEqual(SortOrder.DESCENDING);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('order-key');
    });

    it('should return default ascending if not in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null).mockReturnValueOnce(null);
      const state = service.getSortState('key', 'defaultPredicate');
      expect(state().order).toEqual(SortOrder.ASCENDING);
    });
  });

  describe('setSortState', () => {
    it('should save predicate in localStorage', () => {
      service.setSortState('key', { predicate: 'newPredicate', order: SortOrder.ASCENDING });
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('predicate-key', 'newPredicate');
    });

    it('should save sort order in localStorage', () => {
      service.setSortState('key', { predicate: 'pred', order: SortOrder.DESCENDING });
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('order-key', SortOrder.DESCENDING);
    });
  });
});
