import dayjs from 'dayjs/esm';

import FormatMediumDatetimePipe from './format-medium-datetime.pipe';

describe('FormatMediumDatePipe', () => {
  const formatMediumDatetimePipe = new FormatMediumDatetimePipe();

  it('should return an empty string when receive undefined', () => {
    expect(formatMediumDatetimePipe.transform(undefined)).toBe('');
  });

  it('should return an empty string when receive null', () => {
    expect(formatMediumDatetimePipe.transform(null)).toBe('');
  });

  it('should format the date and time using the browser locale', () => {
    const date = dayjs('2020-11-16');
    const expected = date.toDate().toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'medium' });

    expect(formatMediumDatetimePipe.transform(date)).toBe(expected);
  });
});
