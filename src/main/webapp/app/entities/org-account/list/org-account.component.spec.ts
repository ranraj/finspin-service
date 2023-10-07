import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OrgAccountService } from '../service/org-account.service';

import { OrgAccountComponent } from './org-account.component';

describe('OrgAccount Management Component', () => {
  let comp: OrgAccountComponent;
  let fixture: ComponentFixture<OrgAccountComponent>;
  let service: OrgAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'org-account', component: OrgAccountComponent }]), HttpClientTestingModule],
      declarations: [OrgAccountComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(OrgAccountComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrgAccountComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OrgAccountService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.orgAccounts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
