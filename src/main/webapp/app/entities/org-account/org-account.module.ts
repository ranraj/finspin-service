import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrgAccountComponent } from './list/org-account.component';
import { OrgAccountDetailComponent } from './detail/org-account-detail.component';
import { OrgAccountUpdateComponent } from './update/org-account-update.component';
import { OrgAccountDeleteDialogComponent } from './delete/org-account-delete-dialog.component';
import { OrgAccountRoutingModule } from './route/org-account-routing.module';

@NgModule({
  imports: [SharedModule, OrgAccountRoutingModule],
  declarations: [OrgAccountComponent, OrgAccountDetailComponent, OrgAccountUpdateComponent, OrgAccountDeleteDialogComponent],
  entryComponents: [OrgAccountDeleteDialogComponent],
})
export class OrgAccountModule {}
