import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TileService } from '../service/tile.service';
import { ITile, Tile } from '../tile.model';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { IBoard } from 'app/entities/board/board.model';
import { BoardService } from 'app/entities/board/service/board.service';

import { TileUpdateComponent } from './tile-update.component';

describe('Tile Management Update Component', () => {
  let comp: TileUpdateComponent;
  let fixture: ComponentFixture<TileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tileService: TileService;
  let taskService: TaskService;
  let boardService: BoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TileUpdateComponent],
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
      .overrideTemplate(TileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tileService = TestBed.inject(TileService);
    taskService = TestBed.inject(TaskService);
    boardService = TestBed.inject(BoardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call task query and add missing value', () => {
      const tile: ITile = { id: 'CBA' };
      const task: ITask = { id: '9b945bf2-c1b6-4e63-90c4-311619e7b70d' };
      tile.task = task;

      const taskCollection: ITask[] = [{ id: 'abe7fa34-e3bb-4e18-80a7-7fe597baaa44' }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const expectedCollection: ITask[] = [task, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(taskCollection, task);
      expect(comp.tasksCollection).toEqual(expectedCollection);
    });

    it('Should call Board query and add missing value', () => {
      const tile: ITile = { id: 'CBA' };
      const board: IBoard = { id: '189dd7e9-629b-4ddc-b3da-f5a0a9c0e51d' };
      tile.board = board;

      const boardCollection: IBoard[] = [{ id: 'fdf15441-8033-4221-a235-a56211cb1a90' }];
      jest.spyOn(boardService, 'query').mockReturnValue(of(new HttpResponse({ body: boardCollection })));
      const additionalBoards = [board];
      const expectedCollection: IBoard[] = [...additionalBoards, ...boardCollection];
      jest.spyOn(boardService, 'addBoardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      expect(boardService.query).toHaveBeenCalled();
      expect(boardService.addBoardToCollectionIfMissing).toHaveBeenCalledWith(boardCollection, ...additionalBoards);
      expect(comp.boardsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tile: ITile = { id: 'CBA' };
      const task: ITask = { id: '4ac07dd9-9944-4848-b604-bca50ad74263' };
      tile.task = task;
      const board: IBoard = { id: 'cae6ffff-1a4f-457c-ad9f-8518241d9032' };
      tile.board = board;

      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tile));
      expect(comp.tasksCollection).toContain(task);
      expect(comp.boardsSharedCollection).toContain(board);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tile>>();
      const tile = { id: 'ABC' };
      jest.spyOn(tileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tile }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tileService.update).toHaveBeenCalledWith(tile);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tile>>();
      const tile = new Tile();
      jest.spyOn(tileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tile }));
      saveSubject.complete();

      // THEN
      expect(tileService.create).toHaveBeenCalledWith(tile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tile>>();
      const tile = { id: 'ABC' };
      jest.spyOn(tileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tileService.update).toHaveBeenCalledWith(tile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTaskById', () => {
      it('Should return tracked Task primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackTaskById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackBoardById', () => {
      it('Should return tracked Board primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackBoardById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
