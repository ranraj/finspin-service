import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IDashboard, Dashboard } from '../dashboard.model';
import { DashboardService } from '../service/dashboard.service';

@Component({
  selector: 'jhi-dashboard-update',
  templateUrl: './dashboard-update.component.html',
})
export class DashboardUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    createdDate: [],
    updateDate: [],
  });

  constructor(protected dashboardService: DashboardService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dashboard }) => {
      if (dashboard.id === undefined) {
        const today = dayjs().startOf('day');
        dashboard.createdDate = today;
        dashboard.updateDate = today;
      }

      this.updateForm(dashboard);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dashboard = this.createFromForm();
    if (dashboard.id !== undefined) {
      this.subscribeToSaveResponse(this.dashboardService.update(dashboard));
    } else {
      this.subscribeToSaveResponse(this.dashboardService.create(dashboard));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDashboard>>): void {
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

  protected updateForm(dashboard: IDashboard): void {
    this.editForm.patchValue({
      id: dashboard.id,
      name: dashboard.name,
      createdDate: dashboard.createdDate ? dashboard.createdDate.format(DATE_TIME_FORMAT) : null,
      updateDate: dashboard.updateDate ? dashboard.updateDate.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IDashboard {
    return {
      ...new Dashboard(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateDate: this.editForm.get(['updateDate'])!.value ? dayjs(this.editForm.get(['updateDate'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
