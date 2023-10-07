import { IUser } from 'app/entities/user/user.model';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { IProject } from 'app/entities/project/project.model';

export interface IOrgAccount {
  id?: string;
  name?: string | null;
  blocked?: boolean | null;
  owner?: IUser | null;
  org?: IOrganisation | null;
  projects?: IProject[] | null;
}

export class OrgAccount implements IOrgAccount {
  constructor(
    public id?: string,
    public name?: string | null,
    public blocked?: boolean | null,
    public owner?: IUser | null,
    public org?: IOrganisation | null,
    public projects?: IProject[] | null
  ) {
    this.blocked = this.blocked ?? false;
  }
}

export function getOrgAccountIdentifier(orgAccount: IOrgAccount): string | undefined {
  return orgAccount.id;
}
