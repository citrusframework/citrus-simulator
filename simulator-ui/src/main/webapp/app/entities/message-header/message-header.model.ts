import dayjs from 'dayjs/esm';

export interface IMessageHeader {
  headerId: number;
  name?: string | null;
  value?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}
