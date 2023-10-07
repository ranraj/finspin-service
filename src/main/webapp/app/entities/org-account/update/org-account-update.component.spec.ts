import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrgAccountService } from '../service/org-account.service';
import { IOrgAccount, OrgAccount } from '../org-account.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisation } from 'app/entities/organisation/organisation.model';
import { OrganisationService } from 'app/entities/organisation/service/organisation.service';

import { OrgAccountUpdateComponent } from './org-account-update.component';

describe('OrgAccount Management Update Component', () => {
  let comp: OrgAccountUpdateComponent;
  let fixture: ComponentFixture<OrgAccountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orgAccountService: OrgAccountService;
  let userService: UserService;
  let organisationService: OrganisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrgAccountUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrgAccountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrgAccountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orgAccountService = TestBed.inject(OrgAccountService);
    userService = TestBed.inject(UserService);
    organisationService = TestBed.inject(OrganisationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const orgAccount: IOrgAccount = { id: 'CBA' };
      const owner: IUser = { id: 'bce56ff1-bdca-4c6a-874d-2d419fd50245' };
      orgAccount.owner = owner;

      const userCollection: IUser[] = [{ id: '69f06421-910a-4fb7-a4f9-86496779db50' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [owner];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Organisation query and add missing value', () => {
      const orgAccount: IOrgAccount = { id: 'CBA' };
      const org: IOrganisation = { id: '2a12d527-efe1-4f3d-9c5b-0ad11aca73aa' };
      orgAccount.org = org;

      const organisationCollection: IOrganisation[] = [{ id: '7621ea1d-df36-4556-99be-0c2e5f877550' }];
      jest.spyOn(organisationService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationCollection })));
      const additionalOrganisations = [org];
      const expectedCollection: IOrganisation[] = [...additionalOrganisations, ...organisationCollection];
      jest.spyOn(organisationService, 'addOrganisationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      expect(organisationService.query).toHaveBeenCalled();
      expect(organisationService.addOrganisationToCollectionIfMissing).toHaveBeenCalledWith(
        organisationCollection,
        ...additionalOrganisations
      );
      expect(comp.organisationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orgAccount: IOrgAccount = { id: 'CBA' };
      const owner: IUser = { id: 'cad17a3c-8431-400e-926a-c1e1562aa6b9' };
      orgAccount.owner = owner;
      const org: IOrganisation = { id: '4c95c0ee-abc0-44dc-bff4-90196a23dcda' };
      orgAccount.org = org;

      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(orgAccount));
      expect(comp.usersSharedCollection).toContain(owner);
      expect(comp.organisationsSharedCollection).toContain(org);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgAccount>>();
      const orgAccount = { id: 'ABC' };
      jest.spyOn(orgAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orgAccount }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orgAccountService.update).toHaveBeenCalledWith(orgAccount);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgAccount>>();
      const orgAccount = new OrgAccount();
      jest.spyOn(orgAccountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orgAccount }));
      saveSubject.complete();

      // THEN
      expect(orgAccountService.create).toHaveBeenCalledWith(orgAccount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgAccount>>();
      const orgAccount = { id: 'ABC' };
      jest.spyOn(orgAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orgAccountService.update).toHaveBeenCalledWith(orgAccount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackOrganisationById', () => {
      it('Should return tracked Organisation primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackOrganisationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
