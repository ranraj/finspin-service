import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IOrgGroup } from 'app/entities/org-group/org-group.model';
import { OrgGroupService } from 'app/entities/org-group/service/org-group.service';
import { TaskStatus } from 'app/entities/enumerations/task-status.model';
import { TaskType } from 'app/entities/enumerations/task-type.model';

@Component({
  selector: 'jhi-task-update',
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent implements OnInit {
  isSaving = false;
  taskStatusValues = Object.keys(TaskStatus);
  taskTypeValues = Object.keys(TaskType);

  usersSharedCollection: IUser[] = [];
  sprintsSharedCollection: ISprint[] = [];
  orgGroupsSharedCollection: IOrgGroup[] = [];
  tasksSharedCollection: ITask[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    description: [],
    createDate: [],
    updatedDate: [],
    status: [],
    type: [],
    effortHrs: [],
    startDate: [],
    endDate: [],
    owner: [],
    createdBy: [],
    sprint: [],
    orgGroup: [],
    assignedTo: [],
    parent: [],
    watchers: [],
  });

  constructor(
    protected taskService: TaskService,
    protected userService: UserService,
    protected sprintService: SprintService,
    protected orgGroupService: OrgGroupService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ task }) => {
      if (task.id === undefined) {
        const today = dayjs().startOf('day');
        task.createDate = today;
        task.updatedDate = today;
        task.startDate = today;
        task.endDate = today;
      }

      this.updateForm(task);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const task = this.createFromForm();
    if (task.id !== undefined) {
      this.subscribeToSaveResponse(this.taskService.update(task));
    } else {
      this.subscribeToSaveResponse(this.taskService.create(task));
    }
  }

  trackUserById(_index: number, item: IUser): string {
    return item.id!;
  }

  trackSprintById(_index: number, item: ISprint): string {
    return item.id!;
  }

  trackOrgGroupById(_index: number, item: IOrgGroup): string {
    return item.id!;
  }

  trackTaskById(_index: number, item: ITask): string {
    return item.id!;
  }

  getSelectedUser(option: IUser, selectedVals?: IUser[]): IUser {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITask>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(task: ITask): void {
    this.editForm.patchValue({
      id: task.id,
      title: task.title,
      description: task.description,
      createDate: task.createDate ? task.createDate.format(DATE_TIME_FORMAT) : null,
      updatedDate: task.updatedDate ? task.updatedDate.format(DATE_TIME_FORMAT) : null,
      status: task.status,
      type: task.type,
      effortHrs: task.effortHrs,
      startDate: task.startDate ? task.startDate.format(DATE_TIME_FORMAT) : null,
      endDate: task.endDate ? task.endDate.format(DATE_TIME_FORMAT) : null,
      owner: task.owner,
      createdBy: task.createdBy,
      sprint: task.sprint,
      orgGroup: task.orgGroup,
      assignedTo: task.assignedTo,
      parent: task.parent,
      watchers: task.watchers,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(
      this.usersSharedCollection,
      task.owner,
      task.createdBy,
      task.assignedTo,
      ...(task.watchers ?? [])
    );
    this.sprintsSharedCollection = this.sprintService.addSprintToCollectionIfMissing(this.sprintsSharedCollection, task.sprint);
    this.orgGroupsSharedCollection = this.orgGroupService.addOrgGroupToCollectionIfMissing(this.orgGroupsSharedCollection, task.orgGroup);
    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing(this.tasksSharedCollection, task.parent);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing(
            users,
            this.editForm.get('owner')!.value,
            this.editForm.get('createdBy')!.value,
            this.editForm.get('assignedTo')!.value,
            ...(this.editForm.get('watchers')!.value ?? [])
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.sprintService
      .query()
      .pipe(map((res: HttpResponse<ISprint[]>) => res.body ?? []))
      .pipe(map((sprints: ISprint[]) => this.sprintService.addSprintToCollectionIfMissing(sprints, this.editForm.get('sprint')!.value)))
      .subscribe((sprints: ISprint[]) => (this.sprintsSharedCollection = sprints));

    this.orgGroupService
      .query()
      .pipe(map((res: HttpResponse<IOrgGroup[]>) => res.body ?? []))
      .pipe(
        map((orgGroups: IOrgGroup[]) =>
          this.orgGroupService.addOrgGroupToCollectionIfMissing(orgGroups, this.editForm.get('orgGroup')!.value)
        )
      )
      .subscribe((orgGroups: IOrgGroup[]) => (this.orgGroupsSharedCollection = orgGroups));

    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing(tasks, this.editForm.get('parent')!.value)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      createDate: this.editForm.get(['createDate'])!.value ? dayjs(this.editForm.get(['createDate'])!.value, DATE_TIME_FORMAT) : undefined,
      updatedDate: this.editForm.get(['updatedDate'])!.value
        ? dayjs(this.editForm.get(['updatedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      status: this.editForm.get(['status'])!.value,
      type: this.editForm.get(['type'])!.value,
      effortHrs: this.editForm.get(['effortHrs'])!.value,
      startDate: this.editForm.get(['startDate'])!.value ? dayjs(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      owner: this.editForm.get(['owner'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      sprint: this.editForm.get(['sprint'])!.value,
      orgGroup: this.editForm.get(['orgGroup'])!.value,
      assignedTo: this.editForm.get(['assignedTo'])!.value,
      parent: this.editForm.get(['parent'])!.value,
      watchers: this.editForm.get(['watchers'])!.value,
    };
  }
}
