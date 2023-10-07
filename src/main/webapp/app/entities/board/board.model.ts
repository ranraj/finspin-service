import dayjs from 'dayjs/esm';
import { ITile } from 'app/entities/tile/tile.model';
import { IDashboard } from 'app/entities/dashboard/dashboard.model';

export interface IBoard {
  id?: string;
  title?: string | null;
  uid?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updateDate?: dayjs.Dayjs | null;
  boards?: ITile[] | null;
  dashBoard?: IDashboard | null;
}

export class Board implements IBoard {
  constructor(
    public id?: string,
    public title?: string | null,
    public uid?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public updateDate?: dayjs.Dayjs | null,
    public boards?: ITile[] | null,
    public dashBoard?: IDashboard | null
  ) {}
}

export function getBoardIdentifier(board: IBoard): string | undefined {
  return board.id;
}
