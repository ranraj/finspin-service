import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITile } from '../tile.model';
import { TileService } from '../service/tile.service';

@Component({
  templateUrl: './tile-delete-dialog.component.html',
})
export class TileDeleteDialogComponent {
  tile?: ITile;

  constructor(protected tileService: TileService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.tileService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
