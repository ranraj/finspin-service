import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SprintDetailComponent } from './sprint-detail.component';

describe('Sprint Management Detail Component', () => {
  let comp: SprintDetailComponent;
  let fixture: ComponentFixture<SprintDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SprintDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ sprint: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(SprintDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SprintDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load sprint on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.sprint).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
