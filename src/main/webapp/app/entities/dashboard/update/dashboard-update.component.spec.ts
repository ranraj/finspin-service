import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DashboardService } from '../service/dashboard.service';
import { IDashboard, Dashboard } from '../dashboard.model';

import { DashboardUpdateComponent } from './dashboard-update.component';

describe('Dashboard Management Update Component', () => {
  let comp: DashboardUpdateComponent;
  let fixture: ComponentFixture<DashboardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dashboardService: DashboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DashboardUpdateComponent],
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
      .overrideTemplate(DashboardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DashboardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dashboardService = TestBed.inject(DashboardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const dashboard: IDashboard = { id: 'CBA' };

      activatedRoute.data = of({ dashboard });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(dashboard));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dashboard>>();
      const dashboard = { id: 'ABC' };
      jest.spyOn(dashboardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dashboard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dashboard }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(dashboardService.update).toHaveBeenCalledWith(dashboard);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dashboard>>();
      const dashboard = new Dashboard();
      jest.spyOn(dashboardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dashboard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dashboard }));
      saveSubject.complete();

      // THEN
      expect(dashboardService.create).toHaveBeenCalledWith(dashboard);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dashboard>>();
      const dashboard = { id: 'ABC' };
      jest.spyOn(dashboardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dashboard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dashboardService.update).toHaveBeenCalledWith(dashboard);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
