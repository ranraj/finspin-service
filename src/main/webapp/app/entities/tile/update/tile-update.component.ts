import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITile, Tile } from '../tile.model';
import { TileService } from '../service/tile.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { IBoard } from 'app/entities/board/board.model';
import { BoardService } from 'app/entities/board/service/board.service';
import { PositionMode } from 'app/entities/enumerations/position-mode.model';
import { DisplaySize } from 'app/entities/enumerations/display-size.model';
import { DisplayMode } from 'app/entities/enumerations/display-mode.model';

@Component({
  selector: 'jhi-tile-update',
  templateUrl: './tile-update.component.html',
})
export class TileUpdateComponent implements OnInit {
  isSaving = false;
  positionModeValues = Object.keys(PositionMode);
  displaySizeValues = Object.keys(DisplaySize);
  displayModeValues = Object.keys(DisplayMode);

  tasksCollection: ITask[] = [];
  boardsSharedCollection: IBoard[] = [];

  editForm = this.fb.group({
    id: [],
    positionX: [],
    positionY: [],
    color: [],
    positionMode: [],
    height: [],
    width: [],
    displaySize: [],
    displayMode: [],
    task: [],
    board: [],
  });

  constructor(
    protected tileService: TileService,
    protected taskService: TaskService,
    protected boardService: BoardService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tile }) => {
      this.updateForm(tile);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tile = this.createFromForm();
    if (tile.id !== undefined) {
      this.subscribeToSaveResponse(this.tileService.update(tile));
    } else {
      this.subscribeToSaveResponse(this.tileService.create(tile));
    }
  }

  trackTaskById(_index: number, item: ITask): string {
    return item.id!;
  }

  trackBoardById(_index: number, item: IBoard): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITile>>): void {
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

  protected updateForm(tile: ITile): void {
    this.editForm.patchValue({
      id: tile.id,
      positionX: tile.positionX,
      positionY: tile.positionY,
      color: tile.color,
      positionMode: tile.positionMode,
      height: tile.height,
      width: tile.width,
      displaySize: tile.displaySize,
      displayMode: tile.displayMode,
      task: tile.task,
      board: tile.board,
    });

    this.tasksCollection = this.taskService.addTaskToCollectionIfMissing(this.tasksCollection, tile.task);
    this.boardsSharedCollection = this.boardService.addBoardToCollectionIfMissing(this.boardsSharedCollection, tile.board);
  }

  protected loadRelationshipsOptions(): void {
    this.taskService
      .query({ filter: 'tile-is-null' })
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing(tasks, this.editForm.get('task')!.value)))
      .subscribe((tasks: ITask[]) => (this.tasksCollection = tasks));

    this.boardService
      .query()
      .pipe(map((res: HttpResponse<IBoard[]>) => res.body ?? []))
      .pipe(map((boards: IBoard[]) => this.boardService.addBoardToCollectionIfMissing(boards, this.editForm.get('board')!.value)))
      .subscribe((boards: IBoard[]) => (this.boardsSharedCollection = boards));
  }

  protected createFromForm(): ITile {
    return {
      ...new Tile(),
      id: this.editForm.get(['id'])!.value,
      positionX: this.editForm.get(['positionX'])!.value,
      positionY: this.editForm.get(['positionY'])!.value,
      color: this.editForm.get(['color'])!.value,
      positionMode: this.editForm.get(['positionMode'])!.value,
      height: this.editForm.get(['height'])!.value,
      width: this.editForm.get(['width'])!.value,
      displaySize: this.editForm.get(['displaySize'])!.value,
      displayMode: this.editForm.get(['displayMode'])!.value,
      task: this.editForm.get(['task'])!.value,
      board: this.editForm.get(['board'])!.value,
    };
  }
}
