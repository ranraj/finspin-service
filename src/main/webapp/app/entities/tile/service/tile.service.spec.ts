import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PositionMode } from 'app/entities/enumerations/position-mode.model';
import { DisplaySize } from 'app/entities/enumerations/display-size.model';
import { DisplayMode } from 'app/entities/enumerations/display-mode.model';
import { ITile, Tile } from '../tile.model';

import { TileService } from './tile.service';

describe('Tile Service', () => {
  let service: TileService;
  let httpMock: HttpTestingController;
  let elemDefault: ITile;
  let expectedResult: ITile | ITile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TileService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      positionX: 0,
      positionY: 0,
      color: 'AAAAAAA',
      positionMode: PositionMode.Fixed,
      height: 0,
      width: 0,
      displaySize: DisplaySize.X1,
      displayMode: DisplayMode.Expand,
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

    it('should create a Tile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tile()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          positionX: 1,
          positionY: 1,
          color: 'BBBBBB',
          positionMode: 'BBBBBB',
          height: 1,
          width: 1,
          displaySize: 'BBBBBB',
          displayMode: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tile', () => {
      const patchObject = Object.assign(
        {
          positionX: 1,
          positionY: 1,
          color: 'BBBBBB',
          positionMode: 'BBBBBB',
          height: 1,
          displaySize: 'BBBBBB',
          displayMode: 'BBBBBB',
        },
        new Tile()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          positionX: 1,
          positionY: 1,
          color: 'BBBBBB',
          positionMode: 'BBBBBB',
          height: 1,
          width: 1,
          displaySize: 'BBBBBB',
          displayMode: 'BBBBBB',
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

    it('should delete a Tile', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTileToCollectionIfMissing', () => {
      it('should add a Tile to an empty array', () => {
        const tile: ITile = { id: 'ABC' };
        expectedResult = service.addTileToCollectionIfMissing([], tile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tile);
      });

      it('should not add a Tile to an array that contains it', () => {
        const tile: ITile = { id: 'ABC' };
        const tileCollection: ITile[] = [
          {
            ...tile,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addTileToCollectionIfMissing(tileCollection, tile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tile to an array that doesn't contain it", () => {
        const tile: ITile = { id: 'ABC' };
        const tileCollection: ITile[] = [{ id: 'CBA' }];
        expectedResult = service.addTileToCollectionIfMissing(tileCollection, tile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tile);
      });

      it('should add only unique Tile to an array', () => {
        const tileArray: ITile[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '0067f1d8-91d2-4b05-a851-02b6e693fe99' }];
        const tileCollection: ITile[] = [{ id: 'ABC' }];
        expectedResult = service.addTileToCollectionIfMissing(tileCollection, ...tileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tile: ITile = { id: 'ABC' };
        const tile2: ITile = { id: 'CBA' };
        expectedResult = service.addTileToCollectionIfMissing([], tile, tile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tile);
        expect(expectedResult).toContain(tile2);
      });

      it('should accept null and undefined values', () => {
        const tile: ITile = { id: 'ABC' };
        expectedResult = service.addTileToCollectionIfMissing([], null, tile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tile);
      });

      it('should return initial array if no Tile is added', () => {
        const tileCollection: ITile[] = [{ id: 'ABC' }];
        expectedResult = service.addTileToCollectionIfMissing(tileCollection, undefined, null);
        expect(expectedResult).toEqual(tileCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
