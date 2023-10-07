import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrgGroupComponent } from './list/org-group.component';
import { OrgGroupDetailComponent } from './detail/org-group-detail.component';
import { OrgGroupUpdateComponent } from './update/org-group-update.component';
import { OrgGroupDeleteDialogComponent } from './delete/org-group-delete-dialog.component';
import { OrgGroupRoutingModule } from './route/org-group-routing.module';

@NgModule({
  imports: [SharedModule, OrgGroupRoutingModule],
  declarations: [OrgGroupComponent, OrgGroupDetailComponent, OrgGroupUpdateComponent, OrgGroupDeleteDialogComponent],
  entryComponents: [OrgGroupDeleteDialogComponent],
})
export class OrgGroupModule {}
