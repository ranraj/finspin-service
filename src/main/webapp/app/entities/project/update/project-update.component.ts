import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProject, Project } from '../project.model';
import { ProjectService } from '../service/project.service';
import { IOrgAccount } from 'app/entities/org-account/org-account.model';
import { OrgAccountService } from 'app/entities/org-account/service/org-account.service';

@Component({
  selector: 'jhi-project-update',
  templateUrl: './project-update.component.html',
})
export class ProjectUpdateComponent implements OnInit {
  isSaving = false;

  orgAccountsSharedCollection: IOrgAccount[] = [];

  editForm = this.fb.group({
    id: [],
    string: [null, [Validators.required]],
    createdDate: [],
    updateDate: [],
    orgAccount: [],
  });

  constructor(
    protected projectService: ProjectService,
    protected orgAccountService: OrgAccountService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ project }) => {
      if (project.id === undefined) {
        const today = dayjs().startOf('day');
        project.createdDate = today;
        project.updateDate = today;
      }

      this.updateForm(project);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const project = this.createFromForm();
    if (project.id !== undefined) {
      this.subscribeToSaveResponse(this.projectService.update(project));
    } else {
      this.subscribeToSaveResponse(this.projectService.create(project));
    }
  }

  trackOrgAccountById(_index: number, item: IOrgAccount): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProject>>): void {
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

  protected updateForm(project: IProject): void {
    this.editForm.patchValue({
      id: project.id,
      string: project.string,
      createdDate: project.createdDate ? project.createdDate.format(DATE_TIME_FORMAT) : null,
      updateDate: project.updateDate ? project.updateDate.format(DATE_TIME_FORMAT) : null,
      orgAccount: project.orgAccount,
    });

    this.orgAccountsSharedCollection = this.orgAccountService.addOrgAccountToCollectionIfMissing(
      this.orgAccountsSharedCollection,
      project.orgAccount
    );
  }

  protected loadRelationshipsOptions(): void {
    this.orgAccountService
      .query()
      .pipe(map((res: HttpResponse<IOrgAccount[]>) => res.body ?? []))
      .pipe(
        map((orgAccounts: IOrgAccount[]) =>
          this.orgAccountService.addOrgAccountToCollectionIfMissing(orgAccounts, this.editForm.get('orgAccount')!.value)
        )
      )
      .subscribe((orgAccounts: IOrgAccount[]) => (this.orgAccountsSharedCollection = orgAccounts));
  }

  protected createFromForm(): IProject {
    return {
      ...new Project(),
      id: this.editForm.get(['id'])!.value,
      string: this.editForm.get(['string'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateDate: this.editForm.get(['updateDate'])!.value ? dayjs(this.editForm.get(['updateDate'])!.value, DATE_TIME_FORMAT) : undefined,
      orgAccount: this.editForm.get(['orgAccount'])!.value,
    };
  }
}
