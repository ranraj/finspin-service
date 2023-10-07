import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SprintComponent } from '../list/sprint.component';
import { SprintDetailComponent } from '../detail/sprint-detail.component';
import { SprintUpdateComponent } from '../update/sprint-update.component';
import { SprintRoutingResolveService } from './sprint-routing-resolve.service';

const sprintRoute: Routes = [
  {
    path: '',
    component: SprintComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SprintDetailComponent,
    resolve: {
      sprint: SprintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SprintUpdateComponent,
    resolve: {
      sprint: SprintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SprintUpdateComponent,
    resolve: {
      sprint: SprintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sprintRoute)],
  exports: [RouterModule],
})
export class SprintRoutingModule {}
