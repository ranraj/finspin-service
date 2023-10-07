import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IOrgGroup, getOrgGroupIdentifier } from '../org-group.model';

export type EntityResponseType = HttpResponse<IOrgGroup>;
export type EntityArrayResponseType = HttpResponse<IOrgGroup[]>;

@Injectable({ providedIn: 'root' })
export class OrgGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/org-groups');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/org-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orgGroup: IOrgGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orgGroup);
    return this.http
      .post<IOrgGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(orgGroup: IOrgGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orgGroup);
    return this.http
      .put<IOrgGroup>(`${this.resourceUrl}/${getOrgGroupIdentifier(orgGroup) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(orgGroup: IOrgGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orgGroup);
    return this.http
      .patch<IOrgGroup>(`${this.resourceUrl}/${getOrgGroupIdentifier(orgGroup) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IOrgGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOrgGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOrgGroup[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addOrgGroupToCollectionIfMissing(orgGroupCollection: IOrgGroup[], ...orgGroupsToCheck: (IOrgGroup | null | undefined)[]): IOrgGroup[] {
    const orgGroups: IOrgGroup[] = orgGroupsToCheck.filter(isPresent);
    if (orgGroups.length > 0) {
      const orgGroupCollectionIdentifiers = orgGroupCollection.map(orgGroupItem => getOrgGroupIdentifier(orgGroupItem)!);
      const orgGroupsToAdd = orgGroups.filter(orgGroupItem => {
        const orgGroupIdentifier = getOrgGroupIdentifier(orgGroupItem);
        if (orgGroupIdentifier == null || orgGroupCollectionIdentifiers.includes(orgGroupIdentifier)) {
          return false;
        }
        orgGroupCollectionIdentifiers.push(orgGroupIdentifier);
        return true;
      });
      return [...orgGroupsToAdd, ...orgGroupCollection];
    }
    return orgGroupCollection;
  }

  protected convertDateFromClient(orgGroup: IOrgGroup): IOrgGroup {
    return Object.assign({}, orgGroup, {
      createdDate: orgGroup.createdDate?.isValid() ? orgGroup.createdDate.toJSON() : undefined,
      updateDate: orgGroup.updateDate?.isValid() ? orgGroup.updateDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
      res.body.updateDate = res.body.updateDate ? dayjs(res.body.updateDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((orgGroup: IOrgGroup) => {
        orgGroup.createdDate = orgGroup.createdDate ? dayjs(orgGroup.createdDate) : undefined;
        orgGroup.updateDate = orgGroup.updateDate ? dayjs(orgGroup.updateDate) : undefined;
      });
    }
    return res;
  }
}
