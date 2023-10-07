import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrgGroupService } from '../service/org-group.service';
import { IOrgGroup, OrgGroup } from '../org-group.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

import { OrgGroupUpdateComponent } from './org-group-update.component';

describe('OrgGroup Management Update Component', () => {
  let comp: OrgGroupUpdateComponent;
  let fixture: ComponentFixture<OrgGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orgGroupService: OrgGroupService;
  let userService: UserService;
  let projectService: ProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrgGroupUpdateComponent],
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
      .overrideTemplate(OrgGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrgGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orgGroupService = TestBed.inject(OrgGroupService);
    userService = TestBed.inject(UserService);
    projectService = TestBed.inject(ProjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const orgGroup: IOrgGroup = { id: 'CBA' };
      const head: IUser = { id: 'c6684171-f72f-49b1-bb91-1d7d90f9cee1' };
      orgGroup.head = head;
      const members: IUser[] = [{ id: '25564a15-6a3a-4d35-ac53-0f26db3edbb4' }];
      orgGroup.members = members;

      const userCollection: IUser[] = [{ id: 'eb0fc84c-281c-499a-8bca-8fb55fc8e7bd' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [head, ...members];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Project query and add missing value', () => {
      const orgGroup: IOrgGroup = { id: 'CBA' };
      const project: IProject = { id: '14fae216-6e67-4432-a991-0c71a4ce23bc' };
      orgGroup.project = project;

      const projectCollection: IProject[] = [{ id: '5b267db6-36a4-4388-89c8-1613d2665cf3' }];
      jest.spyOn(projectService, 'query').mockReturnValue(of(new HttpResponse({ body: projectCollection })));
      const additionalProjects = [project];
      const expectedCollection: IProject[] = [...additionalProjects, ...projectCollection];
      jest.spyOn(projectService, 'addProjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      expect(projectService.query).toHaveBeenCalled();
      expect(projectService.addProjectToCollectionIfMissing).toHaveBeenCalledWith(projectCollection, ...additionalProjects);
      expect(comp.projectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orgGroup: IOrgGroup = { id: 'CBA' };
      const head: IUser = { id: '8ac39a86-1747-4a90-99a8-bbca0c856d80' };
      orgGroup.head = head;
      const members: IUser = { id: '0a74a635-0ff4-4764-8f42-e802da8b1e3a' };
      orgGroup.members = [members];
      const project: IProject = { id: 'd727586f-b300-41fe-8368-3f06229fe286' };
      orgGroup.project = project;

      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(orgGroup));
      expect(comp.usersSharedCollection).toContain(head);
      expect(comp.usersSharedCollection).toContain(members);
      expect(comp.projectsSharedCollection).toContain(project);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgGroup>>();
      const orgGroup = { id: 'ABC' };
      jest.spyOn(orgGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orgGroup }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orgGroupService.update).toHaveBeenCalledWith(orgGroup);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgGroup>>();
      const orgGroup = new OrgGroup();
      jest.spyOn(orgGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orgGroup }));
      saveSubject.complete();

      // THEN
      expect(orgGroupService.create).toHaveBeenCalledWith(orgGroup);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrgGroup>>();
      const orgGroup = { id: 'ABC' };
      jest.spyOn(orgGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orgGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orgGroupService.update).toHaveBeenCalledWith(orgGroup);
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

    describe('trackProjectById', () => {
      it('Should return tracked Project primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackProjectById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedUser', () => {
      it('Should return option if no User is selected', () => {
        const option = { id: 'ABC' };
        const result = comp.getSelectedUser(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected User for according option', () => {
        const option = { id: 'ABC' };
        const selected = { id: 'ABC' };
        const selected2 = { id: 'CBA' };
        const result = comp.getSelectedUser(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this User is not selected', () => {
        const option = { id: 'ABC' };
        const selected = { id: 'CBA' };
        const result = comp.getSelectedUser(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
