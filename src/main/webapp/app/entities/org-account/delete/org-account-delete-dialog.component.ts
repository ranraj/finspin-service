import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrgAccount } from '../org-account.model';
import { OrgAccountService } from '../service/org-account.service';

@Component({
  templateUrl: './org-account-delete-dialog.component.html',
})
export class OrgAccountDeleteDialogComponent {
  orgAccount?: IOrgAccount;

  constructor(protected orgAccountService: OrgAccountService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.orgAccountService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
