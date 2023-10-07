import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IComment, Comment } from '../comment.model';

import { CommentService } from './comment.service';

describe('Comment Service', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  let elemDefault: IComment;
  let expectedResult: IComment | IComment[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      createdDate: currentDate,
      updatedDate: currentDate,
      content: 'AAAAAAA',
      disabled: false,
      upVote: 0,
      downVote: 0,
      permLink: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Comment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Comment()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Comment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
          content: 'BBBBBB',
          disabled: true,
          upVote: 1,
          downVote: 1,
          permLink: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Comment', () => {
      const patchObject = Object.assign(
        {
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
          content: 'BBBBBB',
          permLink: 'BBBBBB',
        },
        new Comment()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Comment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
          content: 'BBBBBB',
          disabled: true,
          upVote: 1,
          downVote: 1,
          permLink: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Comment', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCommentToCollectionIfMissing', () => {
      it('should add a Comment to an empty array', () => {
        const comment: IComment = { id: 'ABC' };
        expectedResult = service.addCommentToCollectionIfMissing([], comment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(comment);
      });

      it('should not add a Comment to an array that contains it', () => {
        const comment: IComment = { id: 'ABC' };
        const commentCollection: IComment[] = [
          {
            ...comment,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCommentToCollectionIfMissing(commentCollection, comment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Comment to an array that doesn't contain it", () => {
        const comment: IComment = { id: 'ABC' };
        const commentCollection: IComment[] = [{ id: 'CBA' }];
        expectedResult = service.addCommentToCollectionIfMissing(commentCollection, comment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(comment);
      });

      it('should add only unique Comment to an array', () => {
        const commentArray: IComment[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '66e4887d-4957-4511-8dae-e64f22fe28c4' }];
        const commentCollection: IComment[] = [{ id: 'ABC' }];
        expectedResult = service.addCommentToCollectionIfMissing(commentCollection, ...commentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const comment: IComment = { id: 'ABC' };
        const comment2: IComment = { id: 'CBA' };
        expectedResult = service.addCommentToCollectionIfMissing([], comment, comment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(comment);
        expect(expectedResult).toContain(comment2);
      });

      it('should accept null and undefined values', () => {
        const comment: IComment = { id: 'ABC' };
        expectedResult = service.addCommentToCollectionIfMissing([], null, comment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(comment);
      });

      it('should return initial array if no Comment is added', () => {
        const commentCollection: IComment[] = [{ id: 'ABC' }];
        expectedResult = service.addCommentToCollectionIfMissing(commentCollection, undefined, null);
        expect(expectedResult).toEqual(commentCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
