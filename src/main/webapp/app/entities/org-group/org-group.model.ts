import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IProject } from 'app/entities/project/project.model';
import { ITask } from 'app/entities/task/task.model';

export interface IOrgGroup {
  id?: string;
  countryName?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updateDate?: dayjs.Dayjs | null;
  head?: IUser | null;
  project?: IProject | null;
  members?: IUser[] | null;
  tasks?: ITask[] | null;
}

export class OrgGroup implements IOrgGroup {
  constructor(
    public id?: string,
    public countryName?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public updateDate?: dayjs.Dayjs | null,
    public head?: IUser | null,
    public project?: IProject | null,
    public members?: IUser[] | null,
    public tasks?: ITask[] | null
  ) {}
}

export function getOrgGroupIdentifier(orgGroup: IOrgGroup): string | undefined {
  return orgGroup.id;
}
