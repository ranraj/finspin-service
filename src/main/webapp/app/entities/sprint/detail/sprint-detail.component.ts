import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISprint } from '../sprint.model';

@Component({
  selector: 'jhi-sprint-detail',
  templateUrl: './sprint-detail.component.html',
})
export class SprintDetailComponent implements OnInit {
  sprint: ISprint | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sprint }) => {
      this.sprint = sprint;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
