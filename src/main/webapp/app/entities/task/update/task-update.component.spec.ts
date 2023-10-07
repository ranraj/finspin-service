import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TaskService } from '../service/task.service';
import { ITask, Task } from '../task.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IOrgGroup } from 'app/entities/org-group/org-group.model';
import { OrgGroupService } from 'app/entities/org-group/service/org-group.service';

import { TaskUpdateComponent } from './task-update.component';

describe('Task Management Update Component', () => {
  let comp: TaskUpdateComponent;
  let fixture: ComponentFixture<TaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskService: TaskService;
  let userService: UserService;
  let sprintService: SprintService;
  let orgGroupService: OrgGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TaskUpdateComponent],
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
      .overrideTemplate(TaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskService = TestBed.inject(TaskService);
    userService = TestBed.inject(UserService);
    sprintService = TestBed.inject(SprintService);
    orgGroupService = TestBed.inject(OrgGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const task: ITask = { id: 'CBA' };
      const owner: IUser = { id: '85c84a79-a1a9-435a-89c1-2341fd8d0699' };
      task.owner = owner;
      const createdBy: IUser = { id: '0a45709f-f8a5-4065-a058-4886801195b2' };
      task.createdBy = createdBy;
      const assignedTo: IUser = { id: 'b71f2872-b968-4c3d-9189-3c36b151a73e' };
      task.assignedTo = assignedTo;
      const watchers: IUser[] = [{ id: '46f0a128-3410-4bc4-a560-422111fd5601' }];
      task.watchers = watchers;

      const userCollection: IUser[] = [{ id: 'bb97886c-4780-4868-a491-5b140b9d3367' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [owner, createdBy, assignedTo, ...watchers];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sprint query and add missing value', () => {
      const task: ITask = { id: 'CBA' };
      const sprint: ISprint = { id: '8151590f-3b96-4e94-959d-6ec8095ea1d6' };
      task.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: '47558fa5-ba54-44ef-a910-83f9a91aa672' }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(sprintCollection, ...additionalSprints);
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call OrgGroup query and add missing value', () => {
      const task: ITask = { id: 'CBA' };
      const orgGroup: IOrgGroup = { id: '699afd6e-48c5-4d07-a593-e8a79b8b48c6' };
      task.orgGroup = orgGroup;

      const orgGroupCollection: IOrgGroup[] = [{ id: '7869a789-2a69-4c21-a6bc-a1df71de5e5e' }];
      jest.spyOn(orgGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: orgGroupCollection })));
      const additionalOrgGroups = [orgGroup];
      const expectedCollection: IOrgGroup[] = [...additionalOrgGroups, ...orgGroupCollection];
      jest.spyOn(orgGroupService, 'addOrgGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(orgGroupService.query).toHaveBeenCalled();
      expect(orgGroupService.addOrgGroupToCollectionIfMissing).toHaveBeenCalledWith(orgGroupCollection, ...additionalOrgGroups);
      expect(comp.orgGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Task query and add missing value', () => {
      const task: ITask = { id: 'CBA' };
      const parent: ITask = { id: '7b6b78e3-4724-4259-b976-74fa040f86f2' };
      task.parent = parent;

      const taskCollection: ITask[] = [{ id: '64d3fee7-7ad7-4ac2-bc10-676408c25701' }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [parent];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(taskCollection, ...additionalTasks);
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const task: ITask = { id: 'CBA' };
      const owner: IUser = { id: 'b806a313-e468-4f98-95c1-240e59780680' };
      task.owner = owner;
      const createdBy: IUser = { id: 'fd88b227-9af5-4d2b-88fa-34e78ed2daee' };
      task.createdBy = createdBy;
      const assignedTo: IUser = { id: '1ab069a7-dd3c-4a1a-81ef-77ea5feffd85' };
      task.assignedTo = assignedTo;
      const watchers: IUser = { id: '4433bcbd-8740-4ab6-94ec-7184d39763e9' };
      task.watchers = [watchers];
      const sprint: ISprint = { id: 'e75fca59-267c-4c0c-925a-a9b0ce5c7328' };
      task.sprint = sprint;
      const orgGroup: IOrgGroup = { id: '82e64b90-f8f8-42d4-ae0b-621bc8736601' };
      task.orgGroup = orgGroup;
      const parent: ITask = { id: '170120f1-179b-464c-a913-b989ac33f4c5' };
      task.parent = parent;

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(task));
      expect(comp.usersSharedCollection).toContain(owner);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(assignedTo);
      expect(comp.usersSharedCollection).toContain(watchers);
      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.orgGroupsSharedCollection).toContain(orgGroup);
      expect(comp.tasksSharedCollection).toContain(parent);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 'ABC' };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskService.update).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = new Task();
      jest.spyOn(taskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(taskService.create).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 'ABC' };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskService.update).toHaveBeenCalledWith(task);
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

    describe('trackSprintById', () => {
      it('Should return tracked Sprint primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackSprintById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackOrgGroupById', () => {
      it('Should return tracked OrgGroup primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackOrgGroupById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTaskById', () => {
      it('Should return tracked Task primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackTaskById(0, entity);
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
