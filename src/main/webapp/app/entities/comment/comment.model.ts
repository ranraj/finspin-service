import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ITask } from 'app/entities/task/task.model';

export interface IComment {
  id?: string;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  content?: string | null;
  disabled?: boolean | null;
  upVote?: number | null;
  downVote?: number | null;
  permLink?: string | null;
  owner?: IUser | null;
  task?: ITask | null;
}

export class Comment implements IComment {
  constructor(
    public id?: string,
    public createdDate?: dayjs.Dayjs | null,
    public updatedDate?: dayjs.Dayjs | null,
    public content?: string | null,
    public disabled?: boolean | null,
    public upVote?: number | null,
    public downVote?: number | null,
    public permLink?: string | null,
    public owner?: IUser | null,
    public task?: ITask | null
  ) {
    this.disabled = this.disabled ?? false;
  }
}

export function getCommentIdentifier(comment: IComment): string | undefined {
  return comment.id;
}
