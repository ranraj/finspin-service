import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrgAccountComponent } from '../list/org-account.component';
import { OrgAccountDetailComponent } from '../detail/org-account-detail.component';
import { OrgAccountUpdateComponent } from '../update/org-account-update.component';
import { OrgAccountRoutingResolveService } from './org-account-routing-resolve.service';

const orgAccountRoute: Routes = [
  {
    path: '',
    component: OrgAccountComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrgAccountDetailComponent,
    resolve: {
      orgAccount: OrgAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrgAccountUpdateComponent,
    resolve: {
      orgAccount: OrgAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrgAccountUpdateComponent,
    resolve: {
      orgAccount: OrgAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orgAccountRoute)],
  exports: [RouterModule],
})
export class OrgAccountRoutingModule {}
