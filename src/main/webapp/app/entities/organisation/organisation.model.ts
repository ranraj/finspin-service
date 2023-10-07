import { IOrgAccount } from 'app/entities/org-account/org-account.model';

export interface IOrganisation {
  id?: string;
  name?: string | null;
  blocked?: boolean | null;
  orgAccounts?: IOrgAccount[] | null;
}

export class Organisation implements IOrganisation {
  constructor(public id?: string, public name?: string | null, public blocked?: boolean | null, public orgAccounts?: IOrgAccount[] | null) {
    this.blocked = this.blocked ?? false;
  }
}

export function getOrganisationIdentifier(organisation: IOrganisation): string | undefined {
  return organisation.id;
}
