import dayjs from 'dayjs/esm';

import { FilterOptions, IFilterOptions } from '../filter';

import { formatDateTimeFilterOptions } from './format-date-time-filter-options';

function mockDayJsUtc(date: Date): void {
  dayjs.utc = jest.fn().mockReturnValueOnce(dayjs(date));
}

describe('formatDateTimeFilterOptions', () => {
  const expectFilterOptionsToContain = (formattedOptions: IFilterOptions, name: string, values: string[]): void => {
    expect(formattedOptions.filterOptions).toHaveLength(1);
    expect(formattedOptions.filterOptions[0]).toEqual({ name, values });
  };

  it('should format single valid date value in filter options', () => {
    const initialOptions = new FilterOptions();
    const date = new Date(2023, 10, 16);
    initialOptions.addFilter('dateFilter', date.toISOString());

    mockDayJsUtc(date);

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', ['2023-11-16 00:00:00']);
  });

  it('should not alter non-date filter values', () => {
    const initialOptions = new FilterOptions();
    initialOptions.addFilter('textFilter', 'sampleText');
    initialOptions.addFilter('numericFilter', '1234');

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expect(formattedOptions.filterOptions[0]).toEqual({ name: 'textFilter', values: ['sampleText'] });
    expect(formattedOptions.filterOptions[1]).toEqual({ name: 'numericFilter', values: ['1234'] });
  });

  it('should handle multiple values correctly', () => {
    const initialOptions = new FilterOptions();
    const date1 = new Date(2023, 10, 15);
    const date2 = new Date(2023, 10, 16);
    initialOptions.addFilter('dateFilter', date1.toISOString(), date2.toISOString());

    dayjs.utc = jest.fn().mockReturnValueOnce(dayjs(date1)).mockReturnValueOnce(dayjs(date2));

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', ['2023-11-15 00:00:00', '2023-11-16 00:00:00']);
  });

  it('should ignore invalid date values', () => {
    const initialOptions = new FilterOptions();
    initialOptions.addFilter('dateFilter', 'invalidDate');

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', ['invalidDate']);
  });

  it('can handle combination of in- and valid values', () => {
    const initialOptions = new FilterOptions();
    const date = new Date(2023, 10, 16);
    initialOptions.addFilter('dateFilter', date.toISOString(), 'invalidDate');

    mockDayJsUtc(date);

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', ['2023-11-16 00:00:00', 'invalidDate']);
  });
});
