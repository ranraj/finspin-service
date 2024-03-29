import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BoardDetailComponent } from './board-detail.component';

describe('Board Management Detail Component', () => {
  let comp: BoardDetailComponent;
  let fixture: ComponentFixture<BoardDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BoardDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ board: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(BoardDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BoardDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load board on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.board).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
