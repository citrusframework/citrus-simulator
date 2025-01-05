import dayjs from 'dayjs/esm';

export interface IScenarioAction {
  actionId: number;
  name?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
}
