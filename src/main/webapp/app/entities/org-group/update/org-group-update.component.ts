import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IOrgGroup, OrgGroup } from '../org-group.model';
import { OrgGroupService } from '../service/org-group.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

@Component({
  selector: 'jhi-org-group-update',
  templateUrl: './org-group-update.component.html',
})
export class OrgGroupUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  projectsSharedCollection: IProject[] = [];

  editForm = this.fb.group({
    id: [],
    countryName: [],
    createdDate: [],
    updateDate: [],
    head: [],
    project: [],
    members: [],
  });

  constructor(
    protected orgGroupService: OrgGroupService,
    protected userService: UserService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orgGroup }) => {
      if (orgGroup.id === undefined) {
        const today = dayjs().startOf('day');
        orgGroup.createdDate = today;
        orgGroup.updateDate = today;
      }

      this.updateForm(orgGroup);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orgGroup = this.createFromForm();
    if (orgGroup.id !== undefined) {
      this.subscribeToSaveResponse(this.orgGroupService.update(orgGroup));
    } else {
      this.subscribeToSaveResponse(this.orgGroupService.create(orgGroup));
    }
  }

  trackUserById(_index: number, item: IUser): string {
    return item.id!;
  }

  trackProjectById(_index: number, item: IProject): string {
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrgGroup>>): void {
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

  protected updateForm(orgGroup: IOrgGroup): void {
    this.editForm.patchValue({
      id: orgGroup.id,
      countryName: orgGroup.countryName,
      createdDate: orgGroup.createdDate ? orgGroup.createdDate.format(DATE_TIME_FORMAT) : null,
      updateDate: orgGroup.updateDate ? orgGroup.updateDate.format(DATE_TIME_FORMAT) : null,
      head: orgGroup.head,
      project: orgGroup.project,
      members: orgGroup.members,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(
      this.usersSharedCollection,
      orgGroup.head,
      ...(orgGroup.members ?? [])
    );
    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing(this.projectsSharedCollection, orgGroup.project);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing(
            users,
            this.editForm.get('head')!.value,
            ...(this.editForm.get('members')!.value ?? [])
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing(projects, this.editForm.get('project')!.value))
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }

  protected createFromForm(): IOrgGroup {
    return {
      ...new OrgGroup(),
      id: this.editForm.get(['id'])!.value,
      countryName: this.editForm.get(['countryName'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateDate: this.editForm.get(['updateDate'])!.value ? dayjs(this.editForm.get(['updateDate'])!.value, DATE_TIME_FORMAT) : undefined,
      head: this.editForm.get(['head'])!.value,
      project: this.editForm.get(['project'])!.value,
      members: this.editForm.get(['members'])!.value,
    };
  }
}
