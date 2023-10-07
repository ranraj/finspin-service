import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISprint } from '../sprint.model';
import { SprintService } from '../service/sprint.service';

@Component({
  templateUrl: './sprint-delete-dialog.component.html',
})
export class SprintDeleteDialogComponent {
  sprint?: ISprint;

  constructor(protected sprintService: SprintService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.sprintService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
