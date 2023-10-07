import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOrgAccount, getOrgAccountIdentifier } from '../org-account.model';

export type EntityResponseType = HttpResponse<IOrgAccount>;
export type EntityArrayResponseType = HttpResponse<IOrgAccount[]>;

@Injectable({ providedIn: 'root' })
export class OrgAccountService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/org-accounts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/org-accounts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orgAccount: IOrgAccount): Observable<EntityResponseType> {
    return this.http.post<IOrgAccount>(this.resourceUrl, orgAccount, { observe: 'response' });
  }

  update(orgAccount: IOrgAccount): Observable<EntityResponseType> {
    return this.http.put<IOrgAccount>(`${this.resourceUrl}/${getOrgAccountIdentifier(orgAccount) as string}`, orgAccount, {
      observe: 'response',
    });
  }

  partialUpdate(orgAccount: IOrgAccount): Observable<EntityResponseType> {
    return this.http.patch<IOrgAccount>(`${this.resourceUrl}/${getOrgAccountIdentifier(orgAccount) as string}`, orgAccount, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IOrgAccount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrgAccount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrgAccount[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOrgAccountToCollectionIfMissing(
    orgAccountCollection: IOrgAccount[],
    ...orgAccountsToCheck: (IOrgAccount | null | undefined)[]
  ): IOrgAccount[] {
    const orgAccounts: IOrgAccount[] = orgAccountsToCheck.filter(isPresent);
    if (orgAccounts.length > 0) {
      const orgAccountCollectionIdentifiers = orgAccountCollection.map(orgAccountItem => getOrgAccountIdentifier(orgAccountItem)!);
      const orgAccountsToAdd = orgAccounts.filter(orgAccountItem => {
        const orgAccountIdentifier = getOrgAccountIdentifier(orgAccountItem);
        if (orgAccountIdentifier == null || orgAccountCollectionIdentifiers.includes(orgAccountIdentifier)) {
          return false;
        }
        orgAccountCollectionIdentifiers.push(orgAccountIdentifier);
        return true;
      });
      return [...orgAccountsToAdd, ...orgAccountCollection];
    }
    return orgAccountCollection;
  }
}
