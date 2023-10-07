import dayjs from 'dayjs/esm';
import { IBoard } from 'app/entities/board/board.model';

export interface IDashboard {
  id?: string;
  name?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updateDate?: dayjs.Dayjs | null;
  boards?: IBoard[] | null;
}

export class Dashboard implements IDashboard {
  constructor(
    public id?: string,
    public name?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public updateDate?: dayjs.Dayjs | null,
    public boards?: IBoard[] | null
  ) {}
}

export function getDashboardIdentifier(dashboard: IDashboard): string | undefined {
  return dashboard.id;
}
