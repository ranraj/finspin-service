import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IDashboard, getDashboardIdentifier } from '../dashboard.model';

export type EntityResponseType = HttpResponse<IDashboard>;
export type EntityArrayResponseType = HttpResponse<IDashboard[]>;

@Injectable({ providedIn: 'root' })
export class DashboardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dashboards');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/dashboards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dashboard: IDashboard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dashboard);
    return this.http
      .post<IDashboard>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(dashboard: IDashboard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dashboard);
    return this.http
      .put<IDashboard>(`${this.resourceUrl}/${getDashboardIdentifier(dashboard) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(dashboard: IDashboard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dashboard);
    return this.http
      .patch<IDashboard>(`${this.resourceUrl}/${getDashboardIdentifier(dashboard) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IDashboard>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDashboard[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDashboard[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addDashboardToCollectionIfMissing(
    dashboardCollection: IDashboard[],
    ...dashboardsToCheck: (IDashboard | null | undefined)[]
  ): IDashboard[] {
    const dashboards: IDashboard[] = dashboardsToCheck.filter(isPresent);
    if (dashboards.length > 0) {
      const dashboardCollectionIdentifiers = dashboardCollection.map(dashboardItem => getDashboardIdentifier(dashboardItem)!);
      const dashboardsToAdd = dashboards.filter(dashboardItem => {
        const dashboardIdentifier = getDashboardIdentifier(dashboardItem);
        if (dashboardIdentifier == null || dashboardCollectionIdentifiers.includes(dashboardIdentifier)) {
          return false;
        }
        dashboardCollectionIdentifiers.push(dashboardIdentifier);
        return true;
      });
      return [...dashboardsToAdd, ...dashboardCollection];
    }
    return dashboardCollection;
  }

  protected convertDateFromClient(dashboard: IDashboard): IDashboard {
    return Object.assign({}, dashboard, {
      createdDate: dashboard.createdDate?.isValid() ? dashboard.createdDate.toJSON() : undefined,
      updateDate: dashboard.updateDate?.isValid() ? dashboard.updateDate.toJSON() : undefined,
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
      res.body.forEach((dashboard: IDashboard) => {
        dashboard.createdDate = dashboard.createdDate ? dayjs(dashboard.createdDate) : undefined;
        dashboard.updateDate = dashboard.updateDate ? dayjs(dashboard.updateDate) : undefined;
      });
    }
    return res;
  }
}
