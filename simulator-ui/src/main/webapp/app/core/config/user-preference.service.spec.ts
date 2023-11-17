import { TestBed } from '@angular/core/testing';

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

  describe('setPreferredPageSize', () => {
    it('should return known item from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce('1234');

      const preferredPageSize = service.getPreferredPageSize('key');

      expect(preferredPageSize).toEqual(1234);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('psize-key');
    });

    it('should return default page size if item is not in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValueOnce(null);

      const preferredPageSize = service.getPreferredPageSize('key');

      expect(preferredPageSize).toEqual(ITEMS_PER_PAGE);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('psize-key');
    });
  });

  describe('setPreferredPageSize', () => {
    it('should save item in localStorage', () => {
      service.setPreferredPageSize('key', 1234);

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('psize-key', '1234');
    });
  });
});
