import { TestBed } from '@angular/core/testing';

import { EntityOrder } from 'app/config/navigation.constants';
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

  describe('getPredicate', () => {
    it('should return the predicate from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce('somePredicate');
      const predicate = service.getPredicate('key', 'defaultPredicate');
      expect(predicate).toEqual('somePredicate');
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('predicate-key');
    });

    it('should return the default predicate if none is in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null);
      const predicate = service.getPredicate('key', 'defaultPredicate');
      expect(predicate).toEqual('defaultPredicate');
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('predicate-key');
    });
  });

  describe('setPredicate', () => {
    it('should save the predicate in localStorage', () => {
      service.setPredicate('key', 'newPredicate');
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('predicate-key', 'newPredicate');
    });
  });

  describe('getEntityOrder', () => {
    it('should return the entity order from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce('DESC');
      const order = service.getEntityOrder('key');
      expect(order).toEqual(EntityOrder.DESCENDING);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('order-key');
    });

    it('should return the default entity order if none is in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null);
      const order = service.getEntityOrder('key');
      expect(order).toEqual(EntityOrder.ASCENDING);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('order-key');
    });
  });

  describe('setEntityOrder', () => {
    it('should save the entity order in localStorage', () => {
      service.setEntityOrder('key', EntityOrder.ASCENDING);
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('order-key', 'asc');
    });
  });
});
