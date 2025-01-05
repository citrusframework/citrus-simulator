import dayjs from 'dayjs/esm';

/*
 * Function used to workaround https://github.com/microsoft/TypeScript/issues/16069
 * es2019 alternative `const filteredArr = myArr.flatMap((x) => x ? x : []);`
 */
export function isPresent<T>(t: T | undefined | null): t is T {
  return t !== undefined && t !== null;
}

export const filterNaN = (input: number): number => (isNaN(input) ? 0 : input);

export const sort = (array: any[] | null, predicate: string, ascending = true): any[] | undefined =>
  array?.sort((a: any, b: any) => {
    const aValue = a[predicate];
    const bValue = b[predicate];

    // Handle undefined or null values
    if (aValue == null && bValue == null) {
      return 0;
    }
    if (aValue == null) {
      return 1;
    } // Consider undefined/null values as greater
    if (bValue == null) {
      return -1;
    }

    // Numeric comparison
    if (typeof aValue === 'number' && typeof bValue === 'number') {
      return ascending ? aValue - bValue : bValue - aValue;
    }

    // String comparison
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      return ascending ? aValue.localeCompare(bValue) : bValue.localeCompare(aValue);
    }

    // Date comparison (using dayjs objects)
    if (dayjs.isDayjs(aValue) && dayjs.isDayjs(bValue)) {
      return ascending ? aValue.valueOf() - bValue.valueOf() : bValue.valueOf() - aValue.valueOf();
    }

    // Mixed types or other types: you might want to handle these cases differently
    console.error('Attempting to sort by a property that is not uniformly a number, string, or dayjs date');
    return 0;
  });
