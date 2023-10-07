import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDashboard, Dashboard } from '../dashboard.model';

import { DashboardService } from './dashboard.service';

describe('Dashboard Service', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;
  let elemDefault: IDashboard;
  let expectedResult: IDashboard | IDashboard[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
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

    it('should create a Dashboard', () => {
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

      service.create(new Dashboard()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dashboard', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
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

    it('should partial update a Dashboard', () => {
      const patchObject = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new Dashboard()
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

    it('should return a list of Dashboard', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
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

    it('should delete a Dashboard', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDashboardToCollectionIfMissing', () => {
      it('should add a Dashboard to an empty array', () => {
        const dashboard: IDashboard = { id: 'ABC' };
        expectedResult = service.addDashboardToCollectionIfMissing([], dashboard);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dashboard);
      });

      it('should not add a Dashboard to an array that contains it', () => {
        const dashboard: IDashboard = { id: 'ABC' };
        const dashboardCollection: IDashboard[] = [
          {
            ...dashboard,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addDashboardToCollectionIfMissing(dashboardCollection, dashboard);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dashboard to an array that doesn't contain it", () => {
        const dashboard: IDashboard = { id: 'ABC' };
        const dashboardCollection: IDashboard[] = [{ id: 'CBA' }];
        expectedResult = service.addDashboardToCollectionIfMissing(dashboardCollection, dashboard);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dashboard);
      });

      it('should add only unique Dashboard to an array', () => {
        const dashboardArray: IDashboard[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'be89c856-808d-4049-bdef-c849338958cd' }];
        const dashboardCollection: IDashboard[] = [{ id: 'ABC' }];
        expectedResult = service.addDashboardToCollectionIfMissing(dashboardCollection, ...dashboardArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dashboard: IDashboard = { id: 'ABC' };
        const dashboard2: IDashboard = { id: 'CBA' };
        expectedResult = service.addDashboardToCollectionIfMissing([], dashboard, dashboard2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dashboard);
        expect(expectedResult).toContain(dashboard2);
      });

      it('should accept null and undefined values', () => {
        const dashboard: IDashboard = { id: 'ABC' };
        expectedResult = service.addDashboardToCollectionIfMissing([], null, dashboard, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dashboard);
      });

      it('should return initial array if no Dashboard is added', () => {
        const dashboardCollection: IDashboard[] = [{ id: 'ABC' }];
        expectedResult = service.addDashboardToCollectionIfMissing(dashboardCollection, undefined, null);
        expect(expectedResult).toEqual(dashboardCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
