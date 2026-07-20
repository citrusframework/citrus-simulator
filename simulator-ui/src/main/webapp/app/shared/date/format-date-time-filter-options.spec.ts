import { FilterOptions, IFilterOptions } from '../filter';

import { formatDateTimeFilterOptions } from './format-date-time-filter-options';

const formatLocal = (date: Date): string => date.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'medium' });

describe('formatDateTimeFilterOptions', () => {
  const expectFilterOptionsToContain = (formattedOptions: IFilterOptions, name: string, values: string[]): void => {
    expect(formattedOptions.filterOptions).toHaveLength(1);
    expect(formattedOptions.filterOptions[0]).toEqual({ name, values });
  };

  it('should format single valid date value in filter options using the browser locale', () => {
    const initialOptions = new FilterOptions();
    const date = new Date(2023, 10, 16);
    initialOptions.addFilter('dateFilter', date.toISOString());

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', [formatLocal(date)]);
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

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', [formatLocal(date1), formatLocal(date2)]);
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

    const formattedOptions = formatDateTimeFilterOptions(initialOptions);

    expectFilterOptionsToContain(formattedOptions, 'dateFilter', [formatLocal(date), 'invalidDate']);
  });
});
