import dayjs from 'dayjs/esm';

import { FilterOptions, IFilterOptions } from '../filter';

export const formatDateTimeFilterOptions = (filterOptions: IFilterOptions): IFilterOptions => {
  const filterOptionsCopy = new FilterOptions();
  filterOptions.filterOptions.forEach(filterOption => {
    const values: string[] = [];
    for (const value of filterOption.values.slice()) {
      const parsedValue = dayjs(value);
      if (isNaN(Number(value)) && parsedValue.isValid()) {
        values.push(parsedValue.toDate().toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'medium' }));
      } else {
        values.push(value);
      }
    }
    filterOptionsCopy.addFilter(filterOption.name, ...values);
  });
  return filterOptionsCopy;
};
