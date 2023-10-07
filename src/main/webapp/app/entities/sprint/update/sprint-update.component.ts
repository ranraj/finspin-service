import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ISprint, Sprint } from '../sprint.model';
import { SprintService } from '../service/sprint.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';

@Component({
  selector: 'jhi-sprint-update',
  templateUrl: './sprint-update.component.html',
})
export class SprintUpdateComponent implements OnInit {
  isSaving = false;

  projectsSharedCollection: IProject[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    code: [],
    createdDate: [],
    updateDate: [],
    startDate: [],
    endDate: [],
    project: [],
  });

  constructor(
    protected sprintService: SprintService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sprint }) => {
      if (sprint.id === undefined) {
        const today = dayjs().startOf('day');
        sprint.createdDate = today;
        sprint.updateDate = today;
        sprint.startDate = today;
        sprint.endDate = today;
      }

      this.updateForm(sprint);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sprint = this.createFromForm();
    if (sprint.id !== undefined) {
      this.subscribeToSaveResponse(this.sprintService.update(sprint));
    } else {
      this.subscribeToSaveResponse(this.sprintService.create(sprint));
    }
  }

  trackProjectById(_index: number, item: IProject): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISprint>>): void {
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

  protected updateForm(sprint: ISprint): void {
    this.editForm.patchValue({
      id: sprint.id,
      name: sprint.name,
      code: sprint.code,
      createdDate: sprint.createdDate ? sprint.createdDate.format(DATE_TIME_FORMAT) : null,
      updateDate: sprint.updateDate ? sprint.updateDate.format(DATE_TIME_FORMAT) : null,
      startDate: sprint.startDate ? sprint.startDate.format(DATE_TIME_FORMAT) : null,
      endDate: sprint.endDate ? sprint.endDate.format(DATE_TIME_FORMAT) : null,
      project: sprint.project,
    });

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing(this.projectsSharedCollection, sprint.project);
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(
        map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing(projects, this.editForm.get('project')!.value))
      )
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));
  }

  protected createFromForm(): ISprint {
    return {
      ...new Sprint(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      code: this.editForm.get(['code'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateDate: this.editForm.get(['updateDate'])!.value ? dayjs(this.editForm.get(['updateDate'])!.value, DATE_TIME_FORMAT) : undefined,
      startDate: this.editForm.get(['startDate'])!.value ? dayjs(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      project: this.editForm.get(['project'])!.value,
    };
  }
}
