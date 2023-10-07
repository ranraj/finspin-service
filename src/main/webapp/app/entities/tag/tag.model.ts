import { ITask } from 'app/entities/task/task.model';

export interface ITag {
  id?: string;
  name?: string | null;
  tasks?: ITask[] | null;
}

export class Tag implements ITag {
  constructor(public id?: string, public name?: string | null, public tasks?: ITask[] | null) {}
}

export function getTagIdentifier(tag: ITag): string | undefined {
  return tag.id;
}
