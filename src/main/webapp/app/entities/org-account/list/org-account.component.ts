import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrgAccount } from '../org-account.model';
import { OrgAccountService } from '../service/org-account.service';
import { OrgAccountDeleteDialogComponent } from '../delete/org-account-delete-dialog.component';

@Component({
  selector: 'jhi-org-account',
  templateUrl: './org-account.component.html',
})
export class OrgAccountComponent implements OnInit {
  orgAccounts?: IOrgAccount[];
  isLoading = false;
  currentSearch: string;

  constructor(protected orgAccountService: OrgAccountService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.orgAccountService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IOrgAccount[]>) => {
            this.isLoading = false;
            this.orgAccounts = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.orgAccountService.query().subscribe({
      next: (res: HttpResponse<IOrgAccount[]>) => {
        this.isLoading = false;
        this.orgAccounts = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IOrgAccount): string {
    return item.id!;
  }

  delete(orgAccount: IOrgAccount): void {
    const modalRef = this.modalService.open(OrgAccountDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.orgAccount = orgAccount;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
