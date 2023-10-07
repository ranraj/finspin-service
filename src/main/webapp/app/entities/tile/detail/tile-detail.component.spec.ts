import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TileDetailComponent } from './tile-detail.component';

describe('Tile Management Detail Component', () => {
  let comp: TileDetailComponent;
  let fixture: ComponentFixture<TileDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TileDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tile: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(TileDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TileDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tile on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tile).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
