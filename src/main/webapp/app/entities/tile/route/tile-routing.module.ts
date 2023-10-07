import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TileComponent } from '../list/tile.component';
import { TileDetailComponent } from '../detail/tile-detail.component';
import { TileUpdateComponent } from '../update/tile-update.component';
import { TileRoutingResolveService } from './tile-routing-resolve.service';

const tileRoute: Routes = [
  {
    path: '',
    component: TileComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TileDetailComponent,
    resolve: {
      tile: TileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TileUpdateComponent,
    resolve: {
      tile: TileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TileUpdateComponent,
    resolve: {
      tile: TileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tileRoute)],
  exports: [RouterModule],
})
export class TileRoutingModule {}
