import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrgGroup } from '../org-group.model';

@Component({
  selector: 'jhi-org-group-detail',
  templateUrl: './org-group-detail.component.html',
})
export class OrgGroupDetailComponent implements OnInit {
  orgGroup: IOrgGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orgGroup }) => {
      this.orgGroup = orgGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
