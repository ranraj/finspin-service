import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationService } from '../service/organisation.service';
import { IOrganisation, Organisation } from '../organisation.model';

import { OrganisationUpdateComponent } from './organisation-update.component';

describe('Organisation Management Update Component', () => {
  let comp: OrganisationUpdateComponent;
  let fixture: ComponentFixture<OrganisationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationService: OrganisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUpdateComponent],
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
      .overrideTemplate(OrganisationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationService = TestBed.inject(OrganisationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const organisation: IOrganisation = { id: 'CBA' };

      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(organisation));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Organisation>>();
      const organisation = { id: 'ABC' };
      jest.spyOn(organisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationService.update).toHaveBeenCalledWith(organisation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Organisation>>();
      const organisation = new Organisation();
      jest.spyOn(organisationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisation }));
      saveSubject.complete();

      // THEN
      expect(organisationService.create).toHaveBeenCalledWith(organisation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Organisation>>();
      const organisation = { id: 'ABC' };
      jest.spyOn(organisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationService.update).toHaveBeenCalledWith(organisation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
