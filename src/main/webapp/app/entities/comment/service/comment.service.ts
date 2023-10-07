import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IComment, getCommentIdentifier } from '../comment.model';

export type EntityResponseType = HttpResponse<IComment>;
export type EntityArrayResponseType = HttpResponse<IComment[]>;

@Injectable({ providedIn: 'root' })
export class CommentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/comments');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/comments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(comment: IComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(comment);
    return this.http
      .post<IComment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(comment: IComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(comment);
    return this.http
      .put<IComment>(`${this.resourceUrl}/${getCommentIdentifier(comment) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(comment: IComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(comment);
    return this.http
      .patch<IComment>(`${this.resourceUrl}/${getCommentIdentifier(comment) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IComment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IComment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IComment[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addCommentToCollectionIfMissing(commentCollection: IComment[], ...commentsToCheck: (IComment | null | undefined)[]): IComment[] {
    const comments: IComment[] = commentsToCheck.filter(isPresent);
    if (comments.length > 0) {
      const commentCollectionIdentifiers = commentCollection.map(commentItem => getCommentIdentifier(commentItem)!);
      const commentsToAdd = comments.filter(commentItem => {
        const commentIdentifier = getCommentIdentifier(commentItem);
        if (commentIdentifier == null || commentCollectionIdentifiers.includes(commentIdentifier)) {
          return false;
        }
        commentCollectionIdentifiers.push(commentIdentifier);
        return true;
      });
      return [...commentsToAdd, ...commentCollection];
    }
    return commentCollection;
  }

  protected convertDateFromClient(comment: IComment): IComment {
    return Object.assign({}, comment, {
      createdDate: comment.createdDate?.isValid() ? comment.createdDate.toJSON() : undefined,
      updatedDate: comment.updatedDate?.isValid() ? comment.updatedDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
      res.body.updatedDate = res.body.updatedDate ? dayjs(res.body.updatedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((comment: IComment) => {
        comment.createdDate = comment.createdDate ? dayjs(comment.createdDate) : undefined;
        comment.updatedDate = comment.updatedDate ? dayjs(comment.updatedDate) : undefined;
      });
    }
    return res;
  }
}
