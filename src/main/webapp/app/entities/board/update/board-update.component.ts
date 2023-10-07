import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IBoard, Board } from '../board.model';
import { BoardService } from '../service/board.service';
import { IDashboard } from 'app/entities/dashboard/dashboard.model';
import { DashboardService } from 'app/entities/dashboard/service/dashboard.service';

@Component({
  selector: 'jhi-board-update',
  templateUrl: './board-update.component.html',
})
export class BoardUpdateComponent implements OnInit {
  isSaving = false;

  dashboardsSharedCollection: IDashboard[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    uid: [],
    createdDate: [],
    updateDate: [],
    dashBoard: [],
  });

  constructor(
    protected boardService: BoardService,
    protected dashboardService: DashboardService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ board }) => {
      if (board.id === undefined) {
        const today = dayjs().startOf('day');
        board.createdDate = today;
        board.updateDate = today;
      }

      this.updateForm(board);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const board = this.createFromForm();
    if (board.id !== undefined) {
      this.subscribeToSaveResponse(this.boardService.update(board));
    } else {
      this.subscribeToSaveResponse(this.boardService.create(board));
    }
  }

  trackDashboardById(_index: number, item: IDashboard): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBoard>>): void {
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

  protected updateForm(board: IBoard): void {
    this.editForm.patchValue({
      id: board.id,
      title: board.title,
      uid: board.uid,
      createdDate: board.createdDate ? board.createdDate.format(DATE_TIME_FORMAT) : null,
      updateDate: board.updateDate ? board.updateDate.format(DATE_TIME_FORMAT) : null,
      dashBoard: board.dashBoard,
    });

    this.dashboardsSharedCollection = this.dashboardService.addDashboardToCollectionIfMissing(
      this.dashboardsSharedCollection,
      board.dashBoard
    );
  }

  protected loadRelationshipsOptions(): void {
    this.dashboardService
      .query()
      .pipe(map((res: HttpResponse<IDashboard[]>) => res.body ?? []))
      .pipe(
        map((dashboards: IDashboard[]) =>
          this.dashboardService.addDashboardToCollectionIfMissing(dashboards, this.editForm.get('dashBoard')!.value)
        )
      )
      .subscribe((dashboards: IDashboard[]) => (this.dashboardsSharedCollection = dashboards));
  }

  protected createFromForm(): IBoard {
    return {
      ...new Board(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      uid: this.editForm.get(['uid'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateDate: this.editForm.get(['updateDate'])!.value ? dayjs(this.editForm.get(['updateDate'])!.value, DATE_TIME_FORMAT) : undefined,
      dashBoard: this.editForm.get(['dashBoard'])!.value,
    };
  }
}
