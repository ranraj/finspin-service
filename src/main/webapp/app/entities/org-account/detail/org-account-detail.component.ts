import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrgAccount } from '../org-account.model';

@Component({
  selector: 'jhi-org-account-detail',
  templateUrl: './org-account-detail.component.html',
})
export class OrgAccountDetailComponent implements OnInit {
  orgAccount: IOrgAccount | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orgAccount }) => {
      this.orgAccount = orgAccount;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
