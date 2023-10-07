import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrgGroup } from '../org-group.model';
import { OrgGroupService } from '../service/org-group.service';

@Component({
  templateUrl: './org-group-delete-dialog.component.html',
})
export class OrgGroupDeleteDialogComponent {
  orgGroup?: IOrgGroup;

  constructor(protected orgGroupService: OrgGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.orgGroupService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
