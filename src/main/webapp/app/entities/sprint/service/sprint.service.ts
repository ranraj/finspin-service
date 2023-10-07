import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISprint, getSprintIdentifier } from '../sprint.model';

export type EntityResponseType = HttpResponse<ISprint>;
export type EntityArrayResponseType = HttpResponse<ISprint[]>;

@Injectable({ providedIn: 'root' })
export class SprintService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sprints');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/sprints');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sprint: ISprint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprint);
    return this.http
      .post<ISprint>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(sprint: ISprint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprint);
    return this.http
      .put<ISprint>(`${this.resourceUrl}/${getSprintIdentifier(sprint) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(sprint: ISprint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sprint);
    return this.http
      .patch<ISprint>(`${this.resourceUrl}/${getSprintIdentifier(sprint) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ISprint>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISprint[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISprint[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addSprintToCollectionIfMissing(sprintCollection: ISprint[], ...sprintsToCheck: (ISprint | null | undefined)[]): ISprint[] {
    const sprints: ISprint[] = sprintsToCheck.filter(isPresent);
    if (sprints.length > 0) {
      const sprintCollectionIdentifiers = sprintCollection.map(sprintItem => getSprintIdentifier(sprintItem)!);
      const sprintsToAdd = sprints.filter(sprintItem => {
        const sprintIdentifier = getSprintIdentifier(sprintItem);
        if (sprintIdentifier == null || sprintCollectionIdentifiers.includes(sprintIdentifier)) {
          return false;
        }
        sprintCollectionIdentifiers.push(sprintIdentifier);
        return true;
      });
      return [...sprintsToAdd, ...sprintCollection];
    }
    return sprintCollection;
  }

  protected convertDateFromClient(sprint: ISprint): ISprint {
    return Object.assign({}, sprint, {
      createdDate: sprint.createdDate?.isValid() ? sprint.createdDate.toJSON() : undefined,
      updateDate: sprint.updateDate?.isValid() ? sprint.updateDate.toJSON() : undefined,
      startDate: sprint.startDate?.isValid() ? sprint.startDate.toJSON() : undefined,
      endDate: sprint.endDate?.isValid() ? sprint.endDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
      res.body.updateDate = res.body.updateDate ? dayjs(res.body.updateDate) : undefined;
      res.body.startDate = res.body.startDate ? dayjs(res.body.startDate) : undefined;
      res.body.endDate = res.body.endDate ? dayjs(res.body.endDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((sprint: ISprint) => {
        sprint.createdDate = sprint.createdDate ? dayjs(sprint.createdDate) : undefined;
        sprint.updateDate = sprint.updateDate ? dayjs(sprint.updateDate) : undefined;
        sprint.startDate = sprint.startDate ? dayjs(sprint.startDate) : undefined;
        sprint.endDate = sprint.endDate ? dayjs(sprint.endDate) : undefined;
      });
    }
    return res;
  }
}
