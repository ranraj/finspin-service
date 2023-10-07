import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TileComponent } from './list/tile.component';
import { TileDetailComponent } from './detail/tile-detail.component';
import { TileUpdateComponent } from './update/tile-update.component';
import { TileDeleteDialogComponent } from './delete/tile-delete-dialog.component';
import { TileRoutingModule } from './route/tile-routing.module';

@NgModule({
  imports: [SharedModule, TileRoutingModule],
  declarations: [TileComponent, TileDetailComponent, TileUpdateComponent, TileDeleteDialogComponent],
  entryComponents: [TileDeleteDialogComponent],
})
export class TileModule {}
