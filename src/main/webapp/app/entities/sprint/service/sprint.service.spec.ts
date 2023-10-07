import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISprint, Sprint } from '../sprint.model';

import { SprintService } from './sprint.service';

describe('Sprint Service', () => {
  let service: SprintService;
  let httpMock: HttpTestingController;
  let elemDefault: ISprint;
  let expectedResult: ISprint | ISprint[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SprintService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      code: 'AAAAAAA',
      createdDate: currentDate,
      updateDate: currentDate,
      startDate: currentDate,
      endDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
          startDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Sprint', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
          startDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
          startDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Sprint()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sprint', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          code: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
          startDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
          startDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sprint', () => {
      const patchObject = Object.assign(
        {
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new Sprint()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
          startDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sprint', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          code: 'BBBBBB',
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
          startDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
          updateDate: currentDate,
          startDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Sprint', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSprintToCollectionIfMissing', () => {
      it('should add a Sprint to an empty array', () => {
        const sprint: ISprint = { id: 'ABC' };
        expectedResult = service.addSprintToCollectionIfMissing([], sprint);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprint);
      });

      it('should not add a Sprint to an array that contains it', () => {
        const sprint: ISprint = { id: 'ABC' };
        const sprintCollection: ISprint[] = [
          {
            ...sprint,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addSprintToCollectionIfMissing(sprintCollection, sprint);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sprint to an array that doesn't contain it", () => {
        const sprint: ISprint = { id: 'ABC' };
        const sprintCollection: ISprint[] = [{ id: 'CBA' }];
        expectedResult = service.addSprintToCollectionIfMissing(sprintCollection, sprint);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprint);
      });

      it('should add only unique Sprint to an array', () => {
        const sprintArray: ISprint[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'fd41c48a-97f9-4aa8-8e22-b3656061ccee' }];
        const sprintCollection: ISprint[] = [{ id: 'ABC' }];
        expectedResult = service.addSprintToCollectionIfMissing(sprintCollection, ...sprintArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sprint: ISprint = { id: 'ABC' };
        const sprint2: ISprint = { id: 'CBA' };
        expectedResult = service.addSprintToCollectionIfMissing([], sprint, sprint2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprint);
        expect(expectedResult).toContain(sprint2);
      });

      it('should accept null and undefined values', () => {
        const sprint: ISprint = { id: 'ABC' };
        expectedResult = service.addSprintToCollectionIfMissing([], null, sprint, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprint);
      });

      it('should return initial array if no Sprint is added', () => {
        const sprintCollection: ISprint[] = [{ id: 'ABC' }];
        expectedResult = service.addSprintToCollectionIfMissing(sprintCollection, undefined, null);
        expect(expectedResult).toEqual(sprintCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
