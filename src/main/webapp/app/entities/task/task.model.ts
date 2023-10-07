import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { IOrgGroup } from 'app/entities/org-group/org-group.model';
import { IComment } from 'app/entities/comment/comment.model';
import { ITag } from 'app/entities/tag/tag.model';
import { TaskStatus } from 'app/entities/enumerations/task-status.model';
import { TaskType } from 'app/entities/enumerations/task-type.model';

export interface ITask {
  id?: string;
  title?: string | null;
  description?: string | null;
  createDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  status?: TaskStatus | null;
  type?: TaskType | null;
  effortHrs?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  owner?: IUser | null;
  createdBy?: IUser | null;
  sprint?: ISprint | null;
  orgGroup?: IOrgGroup | null;
  assignedTo?: IUser | null;
  parent?: ITask | null;
  watchers?: IUser[] | null;
  comments?: IComment[] | null;
  tags?: ITag[] | null;
}

export class Task implements ITask {
  constructor(
    public id?: string,
    public title?: string | null,
    public description?: string | null,
    public createDate?: dayjs.Dayjs | null,
    public updatedDate?: dayjs.Dayjs | null,
    public status?: TaskStatus | null,
    public type?: TaskType | null,
    public effortHrs?: number | null,
    public startDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null,
    public owner?: IUser | null,
    public createdBy?: IUser | null,
    public sprint?: ISprint | null,
    public orgGroup?: IOrgGroup | null,
    public assignedTo?: IUser | null,
    public parent?: ITask | null,
    public watchers?: IUser[] | null,
    public comments?: IComment[] | null,
    public tags?: ITag[] | null
  ) {}
}

export function getTaskIdentifier(task: ITask): string | undefined {
  return task.id;
}
