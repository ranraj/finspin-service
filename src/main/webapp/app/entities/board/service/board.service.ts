import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IBoard, getBoardIdentifier } from '../board.model';

export type EntityResponseType = HttpResponse<IBoard>;
export type EntityArrayResponseType = HttpResponse<IBoard[]>;

@Injectable({ providedIn: 'root' })
export class BoardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/boards');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/boards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(board: IBoard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(board);
    return this.http
      .post<IBoard>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(board: IBoard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(board);
    return this.http
      .put<IBoard>(`${this.resourceUrl}/${getBoardIdentifier(board) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(board: IBoard): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(board);
    return this.http
      .patch<IBoard>(`${this.resourceUrl}/${getBoardIdentifier(board) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IBoard>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBoard[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBoard[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addBoardToCollectionIfMissing(boardCollection: IBoard[], ...boardsToCheck: (IBoard | null | undefined)[]): IBoard[] {
    const boards: IBoard[] = boardsToCheck.filter(isPresent);
    if (boards.length > 0) {
      const boardCollectionIdentifiers = boardCollection.map(boardItem => getBoardIdentifier(boardItem)!);
      const boardsToAdd = boards.filter(boardItem => {
        const boardIdentifier = getBoardIdentifier(boardItem);
        if (boardIdentifier == null || boardCollectionIdentifiers.includes(boardIdentifier)) {
          return false;
        }
        boardCollectionIdentifiers.push(boardIdentifier);
        return true;
      });
      return [...boardsToAdd, ...boardCollection];
    }
    return boardCollection;
  }

  protected convertDateFromClient(board: IBoard): IBoard {
    return Object.assign({}, board, {
      createdDate: board.createdDate?.isValid() ? board.createdDate.toJSON() : undefined,
      updateDate: board.updateDate?.isValid() ? board.updateDate.toJSON() : undefined,
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
      res.body.forEach((board: IBoard) => {
        board.createdDate = board.createdDate ? dayjs(board.createdDate) : undefined;
        board.updateDate = board.updateDate ? dayjs(board.updateDate) : undefined;
      });
    }
    return res;
  }
}
