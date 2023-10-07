import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrgGroup, OrgGroup } from '../org-group.model';

import { OrgGroupService } from './org-group.service';

describe('OrgGroup Service', () => {
  let service: OrgGroupService;
  let httpMock: HttpTestingController;
  let elemDefault: IOrgGroup;
  let expectedResult: IOrgGroup | IOrgGroup[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrgGroupService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      countryName: 'AAAAAAA',
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

    it('should create a OrgGroup', () => {
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

      service.create(new OrgGroup()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrgGroup', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          countryName: 'BBBBBB',
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

    it('should partial update a OrgGroup', () => {
      const patchObject = Object.assign(
        {
          countryName: 'BBBBBB',
          updateDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new OrgGroup()
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

    it('should return a list of OrgGroup', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          countryName: 'BBBBBB',
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

    it('should delete a OrgGroup', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOrgGroupToCollectionIfMissing', () => {
      it('should add a OrgGroup to an empty array', () => {
        const orgGroup: IOrgGroup = { id: 'ABC' };
        expectedResult = service.addOrgGroupToCollectionIfMissing([], orgGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orgGroup);
      });

      it('should not add a OrgGroup to an array that contains it', () => {
        const orgGroup: IOrgGroup = { id: 'ABC' };
        const orgGroupCollection: IOrgGroup[] = [
          {
            ...orgGroup,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addOrgGroupToCollectionIfMissing(orgGroupCollection, orgGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrgGroup to an array that doesn't contain it", () => {
        const orgGroup: IOrgGroup = { id: 'ABC' };
        const orgGroupCollection: IOrgGroup[] = [{ id: 'CBA' }];
        expectedResult = service.addOrgGroupToCollectionIfMissing(orgGroupCollection, orgGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orgGroup);
      });

      it('should add only unique OrgGroup to an array', () => {
        const orgGroupArray: IOrgGroup[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '697a001d-3662-46b9-a342-87f2a951832d' }];
        const orgGroupCollection: IOrgGroup[] = [{ id: 'ABC' }];
        expectedResult = service.addOrgGroupToCollectionIfMissing(orgGroupCollection, ...orgGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orgGroup: IOrgGroup = { id: 'ABC' };
        const orgGroup2: IOrgGroup = { id: 'CBA' };
        expectedResult = service.addOrgGroupToCollectionIfMissing([], orgGroup, orgGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orgGroup);
        expect(expectedResult).toContain(orgGroup2);
      });

      it('should accept null and undefined values', () => {
        const orgGroup: IOrgGroup = { id: 'ABC' };
        expectedResult = service.addOrgGroupToCollectionIfMissing([], null, orgGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orgGroup);
      });

      it('should return initial array if no OrgGroup is added', () => {
        const orgGroupCollection: IOrgGroup[] = [{ id: 'ABC' }];
        expectedResult = service.addOrgGroupToCollectionIfMissing(orgGroupCollection, undefined, null);
        expect(expectedResult).toEqual(orgGroupCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
