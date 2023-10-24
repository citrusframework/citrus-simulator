import { createRequestOption } from './request-util';

describe('createRequestOption', () => {
  it('should return an empty HttpParams if no request object is provided', () => {
    const result = createRequestOption();
    expect(result.toString()).toBe('');
  });

  it('should append regular key-value pairs to HttpParams', () => {
    const req = {
      name: 'John',
      age: 30,
    };
    const result = createRequestOption(req);
    expect(result.toString()).toBe('name=John&age=30');
  });

  it('should handle array values and append them to HttpParams', () => {
    const req = {
      ids: [1, 2, 3],
    };
    const result = createRequestOption(req);
    expect(result.toString()).toBe('ids=1&ids=2&ids=3');
  });

  it('should not append keys with undefined or empty string values', () => {
    const req = {
      name: 'John',
      age: undefined,
      city: '',
    };
    const result = createRequestOption(req);
    expect(result.toString()).toBe('name=John');
  });

  it('should append sort values correctly', () => {
    const req = {
      sort: ['name,asc', 'age,desc'],
    };
    const result = createRequestOption(req);
    expect(result.toString()).toBe('sort=name,asc&sort=age,desc');
  });

  it('should handle a mix of regular and sort key-value pairs', () => {
    const req = {
      name: 'John',
      age: 30,
      sort: ['name,asc'],
    };
    const result = createRequestOption(req);
    expect(result.toString()).toBe('name=John&age=30&sort=name,asc');
  });
});
