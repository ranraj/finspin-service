import { ITask } from 'app/entities/task/task.model';
import { IBoard } from 'app/entities/board/board.model';
import { PositionMode } from 'app/entities/enumerations/position-mode.model';
import { DisplaySize } from 'app/entities/enumerations/display-size.model';
import { DisplayMode } from 'app/entities/enumerations/display-mode.model';

export interface ITile {
  id?: string;
  positionX?: number | null;
  positionY?: number | null;
  color?: string | null;
  positionMode?: PositionMode | null;
  height?: number | null;
  width?: number | null;
  displaySize?: DisplaySize | null;
  displayMode?: DisplayMode | null;
  task?: ITask | null;
  board?: IBoard | null;
}

export class Tile implements ITile {
  constructor(
    public id?: string,
    public positionX?: number | null,
    public positionY?: number | null,
    public color?: string | null,
    public positionMode?: PositionMode | null,
    public height?: number | null,
    public width?: number | null,
    public displaySize?: DisplaySize | null,
    public displayMode?: DisplayMode | null,
    public task?: ITask | null,
    public board?: IBoard | null
  ) {}
}

export function getTileIdentifier(tile: ITile): string | undefined {
  return tile.id;
}
