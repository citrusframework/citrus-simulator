import dayjs from 'dayjs/esm';

import { FilterOptions, IFilterOptions } from '../filter';

export const formatDateTimeFilterOptions = (filterOptions: IFilterOptions): IFilterOptions => {
  const filterOptionsCopy = new FilterOptions();
  filterOptions.filterOptions.forEach(filterOption => {
    const values: string[] = [];
    for (const value of filterOption.values.slice()) {
      if (isNaN(Number(value)) && dayjs(value).isValid()) {
        values.push(dayjs(value).format('YYYY-MM-DD HH:mm:ss'));
      } else {
        values.push(value);
      }
    }
    filterOptionsCopy.addFilter(filterOption.name, ...values);
  });
  return filterOptionsCopy;
};
