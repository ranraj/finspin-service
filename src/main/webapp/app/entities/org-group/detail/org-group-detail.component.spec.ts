import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrgGroupDetailComponent } from './org-group-detail.component';

describe('OrgGroup Management Detail Component', () => {
  let comp: OrgGroupDetailComponent;
  let fixture: ComponentFixture<OrgGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrgGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orgGroup: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(OrgGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrgGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orgGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orgGroup).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
