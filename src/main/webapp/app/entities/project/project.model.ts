import dayjs from 'dayjs/esm';
import { IOrgAccount } from 'app/entities/org-account/org-account.model';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { IOrgGroup } from 'app/entities/org-group/org-group.model';

export interface IProject {
  id?: string;
  string?: string;
  createdDate?: dayjs.Dayjs | null;
  updateDate?: dayjs.Dayjs | null;
  orgAccount?: IOrgAccount | null;
  sprints?: ISprint[] | null;
  orgGroups?: IOrgGroup[] | null;
}

export class Project implements IProject {
  constructor(
    public id?: string,
    public string?: string,
    public createdDate?: dayjs.Dayjs | null,
    public updateDate?: dayjs.Dayjs | null,
    public orgAccount?: IOrgAccount | null,
    public sprints?: ISprint[] | null,
    public orgGroups?: IOrgGroup[] | null
  ) {}
}

export function getProjectIdentifier(project: IProject): string | undefined {
  return project.id;
}
