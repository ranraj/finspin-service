import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrgGroupComponent } from '../list/org-group.component';
import { OrgGroupDetailComponent } from '../detail/org-group-detail.component';
import { OrgGroupUpdateComponent } from '../update/org-group-update.component';
import { OrgGroupRoutingResolveService } from './org-group-routing-resolve.service';

const orgGroupRoute: Routes = [
  {
    path: '',
    component: OrgGroupComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrgGroupDetailComponent,
    resolve: {
      orgGroup: OrgGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrgGroupUpdateComponent,
    resolve: {
      orgGroup: OrgGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrgGroupUpdateComponent,
    resolve: {
      orgGroup: OrgGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orgGroupRoute)],
  exports: [RouterModule],
})
export class OrgGroupRoutingModule {}
