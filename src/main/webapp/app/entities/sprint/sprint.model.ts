import dayjs from 'dayjs/esm';
import { IProject } from 'app/entities/project/project.model';
import { ITask } from 'app/entities/task/task.model';

export interface ISprint {
  id?: string;
  name?: string | null;
  code?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updateDate?: dayjs.Dayjs | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  project?: IProject | null;
  tasks?: ITask[] | null;
}

export class Sprint implements ISprint {
  constructor(
    public id?: string,
    public name?: string | null,
    public code?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public updateDate?: dayjs.Dayjs | null,
    public startDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null,
    public project?: IProject | null,
    public tasks?: ITask[] | null
  ) {}
}

export function getSprintIdentifier(sprint: ISprint): string | undefined {
  return sprint.id;
}
