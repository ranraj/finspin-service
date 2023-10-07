import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBoard, Board } from '../board.model';

import { BoardService } from './board.service';

describe('Board Service', () => {
  let service: BoardService;
  let httpMock: HttpTestingController;
  let elemDefault: IBoard;
  let expectedResult: IBoard | IBoard[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BoardService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      title: 'AAAAAAA',
      uid: 'AAAAAAA',
      createdDate: currentDate,
      updateDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Board', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Board()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Board', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          title: 'BBBBBB',
          uid: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Board', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
          uid: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new Board()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Board', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          title: 'BBBBBB',
          uid: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Board', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBoardToCollectionIfMissing', () => {
      it('should add a Board to an empty array', () => {
        const board: IBoard = { id: 'ABC' };
        expectedResult = service.addBoardToCollectionIfMissing([], board);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(board);
      });

      it('should not add a Board to an array that contains it', () => {
        const board: IBoard = { id: 'ABC' };
        const boardCollection: IBoard[] = [
          {
            ...board,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, board);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Board to an array that doesn't contain it", () => {
        const board: IBoard = { id: 'ABC' };
        const boardCollection: IBoard[] = [{ id: 'CBA' }];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, board);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(board);
      });

      it('should add only unique Board to an array', () => {
        const boardArray: IBoard[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '85a2ed14-3f07-4a04-a081-1e927b3255bc' }];
        const boardCollection: IBoard[] = [{ id: 'ABC' }];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, ...boardArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const board: IBoard = { id: 'ABC' };
        const board2: IBoard = { id: 'CBA' };
        expectedResult = service.addBoardToCollectionIfMissing([], board, board2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(board);
        expect(expectedResult).toContain(board2);
      });

      it('should accept null and undefined values', () => {
        const board: IBoard = { id: 'ABC' };
        expectedResult = service.addBoardToCollectionIfMissing([], null, board, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(board);
      });

      it('should return initial array if no Board is added', () => {
        const boardCollection: IBoard[] = [{ id: 'ABC' }];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, undefined, null);
        expect(expectedResult).toEqual(boardCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
