import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITag } from '../tag.model';
import { TagService } from '../service/tag.service';
import { TagDeleteDialogComponent } from '../delete/tag-delete-dialog.component';

@Component({
  selector: 'jhi-tag',
  templateUrl: './tag.component.html',
})
export class TagComponent implements OnInit {
  tags?: ITag[];
  isLoading = false;
  currentSearch: string;

  constructor(protected tagService: TagService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.tagService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ITag[]>) => {
            this.isLoading = false;
            this.tags = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.tagService.query().subscribe({
      next: (res: HttpResponse<ITag[]>) => {
        this.isLoading = false;
        this.tags = res.body ?? [];
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

  trackId(_index: number, item: ITag): string {
    return item.id!;
  }

  delete(tag: ITag): void {
    const modalRef = this.modalService.open(TagDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tag = tag;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
