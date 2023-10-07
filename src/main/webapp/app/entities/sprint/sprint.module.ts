import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SprintComponent } from './list/sprint.component';
import { SprintDetailComponent } from './detail/sprint-detail.component';
import { SprintUpdateComponent } from './update/sprint-update.component';
import { SprintDeleteDialogComponent } from './delete/sprint-delete-dialog.component';
import { SprintRoutingModule } from './route/sprint-routing.module';

@NgModule({
  imports: [SharedModule, SprintRoutingModule],
  declarations: [SprintComponent, SprintDetailComponent, SprintUpdateComponent, SprintDeleteDialogComponent],
  entryComponents: [SprintDeleteDialogComponent],
})
export class SprintModule {}
