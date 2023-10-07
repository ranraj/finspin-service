import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SprintService } from '../service/sprint.service';
import { ISprint, Sprint } from '../sprint.model';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { SprintUpdateComponent } from './sprint-update.component';

describe('Sprint Management Update Component', () => {
  let comp: SprintUpdateComponent;
  let fixture: ComponentFixture<SprintUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sprintService: SprintService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SprintUpdateComponent],
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
      .overrideTemplate(SprintUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SprintUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sprintService = TestBed.inject(SprintService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Project query and add missing value', () => {
      const sprint: ISprint = { id: 'CBA' };
      const project: IProject = { id: 'ab9a76df-3729-49a8-9010-d4897d5fba3a' };
      sprint.project = project;

      const projectCollection: IProject[] = [{ id: '7b62256a-138a-4e1f-85d3-4ab1a51cff50' }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprint });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(projectCollection, ...additionalProjects);
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sprint: ISprint = { id: 'CBA' };
      const project: IProject = { id: '2086ae72-e66e-408e-898c-087d893ade41' };
      sprint.project = project;

      activatedRoute.data = of({ sprint });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(sprint));
      expect(comp.projectsSharedCollection).toContain(project);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sprint>>();
      const sprint = { id: 'ABC' };
      jest.spyOn(sprintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprint }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(sprintService.update).toHaveBeenCalledWith(sprint);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sprint>>();
      const sprint = new Sprint();
      jest.spyOn(sprintService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprint }));
      saveSubject.complete();

      // THEN
      expect(sprintService.create).toHaveBeenCalledWith(sprint);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sprint>>();
      const sprint = { id: 'ABC' };
      jest.spyOn(sprintService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sprintService.update).toHaveBeenCalledWith(sprint);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProjectById', () => {
      it('Should return tracked Project primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackProjectById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
