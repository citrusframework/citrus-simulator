import { Pipe, PipeTransform } from '@angular/core';

import dayjs from 'dayjs/esm';

@Pipe({
  name: 'formatMediumDatetime',
})
export default class FormatMediumDatetimePipe implements PipeTransform {
  transform(day: dayjs.Dayjs | string | null | undefined): string {
    if (!day) return '';
    const d = dayjs.isDayjs(day) ? day : dayjs(day);
    return d.isValid() ? d.format('D MMM YYYY HH:mm:ss') : '';
  }
}
