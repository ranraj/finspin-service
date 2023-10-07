import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITile, getTileIdentifier } from '../tile.model';

export type EntityResponseType = HttpResponse<ITile>;
export type EntityArrayResponseType = HttpResponse<ITile[]>;

@Injectable({ providedIn: 'root' })
export class TileService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tiles');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/tiles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tile: ITile): Observable<EntityResponseType> {
    return this.http.post<ITile>(this.resourceUrl, tile, { observe: 'response' });
  }

  update(tile: ITile): Observable<EntityResponseType> {
    return this.http.put<ITile>(`${this.resourceUrl}/${getTileIdentifier(tile) as string}`, tile, { observe: 'response' });
  }

  partialUpdate(tile: ITile): Observable<EntityResponseType> {
    return this.http.patch<ITile>(`${this.resourceUrl}/${getTileIdentifier(tile) as string}`, tile, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ITile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITile[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addTileToCollectionIfMissing(tileCollection: ITile[], ...tilesToCheck: (ITile | null | undefined)[]): ITile[] {
    const tiles: ITile[] = tilesToCheck.filter(isPresent);
    if (tiles.length > 0) {
      const tileCollectionIdentifiers = tileCollection.map(tileItem => getTileIdentifier(tileItem)!);
      const tilesToAdd = tiles.filter(tileItem => {
        const tileIdentifier = getTileIdentifier(tileItem);
        if (tileIdentifier == null || tileCollectionIdentifiers.includes(tileIdentifier)) {
          return false;
        }
        tileCollectionIdentifiers.push(tileIdentifier);
        return true;
      });
      return [...tilesToAdd, ...tileCollection];
    }
    return tileCollection;
  }
}
