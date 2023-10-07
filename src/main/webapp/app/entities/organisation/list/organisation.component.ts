import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrganisation } from '../organisation.model';
import { OrganisationService } from '../service/organisation.service';
import { OrganisationDeleteDialogComponent } from '../delete/organisation-delete-dialog.component';

@Component({
  selector: 'jhi-organisation',
  templateUrl: './organisation.component.html',
})
export class OrganisationComponent implements OnInit {
  organisations?: IOrganisation[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected organisationService: OrganisationService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.organisationService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IOrganisation[]>) => {
            this.isLoading = false;
            this.organisations = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.organisationService.query().subscribe({
      next: (res: HttpResponse<IOrganisation[]>) => {
        this.isLoading = false;
        this.organisations = res.body ?? [];
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

  trackId(_index: number, item: IOrganisation): string {
    return item.id!;
  }

  delete(organisation: IOrganisation): void {
    const modalRef = this.modalService.open(OrganisationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.organisation = organisation;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
