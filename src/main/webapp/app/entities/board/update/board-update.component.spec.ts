import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BoardService } from '../service/board.service';
import { IBoard, Board } from '../board.model';
import { IDashboard } from 'app/entities/dashboard/dashboard.model';
import { DashboardService } from 'app/entities/dashboard/service/dashboard.service';

import { BoardUpdateComponent } from './board-update.component';

describe('Board Management Update Component', () => {
  let comp: BoardUpdateComponent;
  let fixture: ComponentFixture<BoardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let boardService: BoardService;
  let dashboardService: DashboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BoardUpdateComponent],
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
      .overrideTemplate(BoardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BoardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    boardService = TestBed.inject(BoardService);
    dashboardService = TestBed.inject(DashboardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Dashboard query and add missing value', () => {
      const board: IBoard = { id: 'CBA' };
      const dashBoard: IDashboard = { id: '6acb1ece-d980-4e7f-b810-a8d5d833ef1e' };
      board.dashBoard = dashBoard;

      const dashboardCollection: IDashboard[] = [{ id: '849016e8-00f9-445a-9425-d36443985007' }];
      jest.spyOn(dashboardService, 'query').mockReturnValue(of(new HttpResponse({ body: dashboardCollection })));
      const additionalDashboards = [dashBoard];
      const expectedCollection: IDashboard[] = [...additionalDashboards, ...dashboardCollection];
      jest.spyOn(dashboardService, 'addDashboardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ board });
      comp.ngOnInit();

      expect(dashboardService.query).toHaveBeenCalled();
      expect(dashboardService.addDashboardToCollectionIfMissing).toHaveBeenCalledWith(dashboardCollection, ...additionalDashboards);
      expect(comp.dashboardsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const board: IBoard = { id: 'CBA' };
      const dashBoard: IDashboard = { id: '8d4dd117-2bc5-455c-af1f-9f52b7417444' };
      board.dashBoard = dashBoard;

      activatedRoute.data = of({ board });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(board));
      expect(comp.dashboardsSharedCollection).toContain(dashBoard);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Board>>();
      const board = { id: 'ABC' };
      jest.spyOn(boardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: board }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(boardService.update).toHaveBeenCalledWith(board);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Board>>();
      const board = new Board();
      jest.spyOn(boardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: board }));
      saveSubject.complete();

      // THEN
      expect(boardService.create).toHaveBeenCalledWith(board);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Board>>();
      const board = { id: 'ABC' };
      jest.spyOn(boardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(boardService.update).toHaveBeenCalledWith(board);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDashboardById', () => {
      it('Should return tracked Dashboard primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackDashboardById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
