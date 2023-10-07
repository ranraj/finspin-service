import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrgAccountDetailComponent } from './org-account-detail.component';

describe('OrgAccount Management Detail Component', () => {
  let comp: OrgAccountDetailComponent;
  let fixture: ComponentFixture<OrgAccountDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrgAccountDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orgAccount: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(OrgAccountDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrgAccountDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orgAccount on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orgAccount).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
