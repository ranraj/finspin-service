import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IOrganisation, getOrganisationIdentifier } from '../organisation.model';

export type EntityResponseType = HttpResponse<IOrganisation>;
export type EntityArrayResponseType = HttpResponse<IOrganisation[]>;

@Injectable({ providedIn: 'root' })
export class OrganisationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/organisations');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/organisations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(organisation: IOrganisation): Observable<EntityResponseType> {
    return this.http.post<IOrganisation>(this.resourceUrl, organisation, { observe: 'response' });
  }

  update(organisation: IOrganisation): Observable<EntityResponseType> {
    return this.http.put<IOrganisation>(`${this.resourceUrl}/${getOrganisationIdentifier(organisation) as string}`, organisation, {
      observe: 'response',
    });
  }

  partialUpdate(organisation: IOrganisation): Observable<EntityResponseType> {
    return this.http.patch<IOrganisation>(`${this.resourceUrl}/${getOrganisationIdentifier(organisation) as string}`, organisation, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IOrganisation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrganisation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrganisation[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addOrganisationToCollectionIfMissing(
    organisationCollection: IOrganisation[],
    ...organisationsToCheck: (IOrganisation | null | undefined)[]
  ): IOrganisation[] {
    const organisations: IOrganisation[] = organisationsToCheck.filter(isPresent);
    if (organisations.length > 0) {
      const organisationCollectionIdentifiers = organisationCollection.map(
        organisationItem => getOrganisationIdentifier(organisationItem)!
      );
      const organisationsToAdd = organisations.filter(organisationItem => {
        const organisationIdentifier = getOrganisationIdentifier(organisationItem);
        if (organisationIdentifier == null || organisationCollectionIdentifiers.includes(organisationIdentifier)) {
          return false;
        }
        organisationCollectionIdentifiers.push(organisationIdentifier);
        return true;
      });
      return [...organisationsToAdd, ...organisationCollection];
    }
    return organisationCollection;
  }
}
