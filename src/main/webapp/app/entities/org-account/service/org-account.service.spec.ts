import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrgAccount, OrgAccount } from '../org-account.model';

import { OrgAccountService } from './org-account.service';

describe('OrgAccount Service', () => {
  let service: OrgAccountService;
  let httpMock: HttpTestingController;
  let elemDefault: IOrgAccount;
  let expectedResult: IOrgAccount | IOrgAccount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrgAccountService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      blocked: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a OrgAccount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new OrgAccount()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrgAccount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          blocked: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrgAccount', () => {
      const patchObject = Object.assign({}, new OrgAccount());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrgAccount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          blocked: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a OrgAccount', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOrgAccountToCollectionIfMissing', () => {
      it('should add a OrgAccount to an empty array', () => {
        const orgAccount: IOrgAccount = { id: 'ABC' };
        expectedResult = service.addOrgAccountToCollectionIfMissing([], orgAccount);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orgAccount);
      });

      it('should not add a OrgAccount to an array that contains it', () => {
        const orgAccount: IOrgAccount = { id: 'ABC' };
        const orgAccountCollection: IOrgAccount[] = [
          {
            ...orgAccount,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addOrgAccountToCollectionIfMissing(orgAccountCollection, orgAccount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrgAccount to an array that doesn't contain it", () => {
        const orgAccount: IOrgAccount = { id: 'ABC' };
        const orgAccountCollection: IOrgAccount[] = [{ id: 'CBA' }];
        expectedResult = service.addOrgAccountToCollectionIfMissing(orgAccountCollection, orgAccount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orgAccount);
      });

      it('should add only unique OrgAccount to an array', () => {
        const orgAccountArray: IOrgAccount[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '43f0adad-750e-4b0c-b9a9-a7d3201d0d33' }];
        const orgAccountCollection: IOrgAccount[] = [{ id: 'ABC' }];
        expectedResult = service.addOrgAccountToCollectionIfMissing(orgAccountCollection, ...orgAccountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orgAccount: IOrgAccount = { id: 'ABC' };
        const orgAccount2: IOrgAccount = { id: 'CBA' };
        expectedResult = service.addOrgAccountToCollectionIfMissing([], orgAccount, orgAccount2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orgAccount);
        expect(expectedResult).toContain(orgAccount2);
      });

      it('should accept null and undefined values', () => {
        const orgAccount: IOrgAccount = { id: 'ABC' };
        expectedResult = service.addOrgAccountToCollectionIfMissing([], null, orgAccount, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orgAccount);
      });

      it('should return initial array if no OrgAccount is added', () => {
        const orgAccountCollection: IOrgAccount[] = [{ id: 'ABC' }];
        expectedResult = service.addOrgAccountToCollectionIfMissing(orgAccountCollection, undefined, null);
        expect(expectedResult).toEqual(orgAccountCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
