import { TestBed } from '@angular/core/testing';
import { StateStorageService } from './state-storage.service';

type Mock = jest.Mock;

describe('StateStorageService', () => {
  let service: StateStorageService;
  let mockSessionStorage: Partial<Storage> & {
    setItem: Mock;
    getItem: Mock;
    removeItem: Mock;
  };

  beforeEach(() => {
    mockSessionStorage = {
      setItem: jest.fn(),
      getItem: jest.fn(),
      removeItem: jest.fn(),
    };

    Object.defineProperty(window, 'sessionStorage', { value: mockSessionStorage, writable: true });

    TestBed.configureTestingModule({
      providers: [StateStorageService],
    });

    service = TestBed.inject(StateStorageService);
  });

  it('should store the URL', () => {
    service.storeUrl('test-url');
    expect(mockSessionStorage.setItem).toHaveBeenCalledWith('previousUrl', '"test-url"');
  });

  it('should retrieve the stored URL', () => {
    mockSessionStorage.getItem.mockReturnValue('"test-url"');
    const url = service.getUrl();
    expect(url).toEqual('test-url');
  });

  it('should return null if no URL stored', () => {
    mockSessionStorage.getItem.mockReturnValue(null);
    const url = service.getUrl();
    expect(url).toBeNull();
  });

  it('should clear the stored URL', () => {
    service.clearUrl();
    expect(mockSessionStorage.removeItem).toHaveBeenCalledWith('previousUrl');
  });

  it('should store the locale', () => {
    service.storeLocale('en');
    expect(mockSessionStorage.setItem).toHaveBeenCalledWith('locale', 'en');
  });

  it('should retrieve the stored locale', () => {
    mockSessionStorage.getItem.mockReturnValue('en');
    const locale = service.getLocale();
    expect(locale).toEqual('en');
  });

  it('should return null if no locale stored', () => {
    mockSessionStorage.getItem.mockReturnValue(null);
    const locale = service.getLocale();
    expect(locale).toBeNull();
  });

  it('should clear the stored locale', () => {
    service.clearLocale();
    expect(mockSessionStorage.removeItem).toHaveBeenCalledWith('locale');
  });
});
