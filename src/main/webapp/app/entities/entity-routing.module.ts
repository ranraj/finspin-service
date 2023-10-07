import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'organisation',
        data: { pageTitle: 'finspinApp.organisation.home.title' },
        loadChildren: () => import('./organisation/organisation.module').then(m => m.OrganisationModule),
      },
      {
        path: 'org-account',
        data: { pageTitle: 'finspinApp.orgAccount.home.title' },
        loadChildren: () => import('./org-account/org-account.module').then(m => m.OrgAccountModule),
      },
      {
        path: 'org-group',
        data: { pageTitle: 'finspinApp.orgGroup.home.title' },
        loadChildren: () => import('./org-group/org-group.module').then(m => m.OrgGroupModule),
      },
      {
        path: 'sprint',
        data: { pageTitle: 'finspinApp.sprint.home.title' },
        loadChildren: () => import('./sprint/sprint.module').then(m => m.SprintModule),
      },
      {
        path: 'project',
        data: { pageTitle: 'finspinApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'finspinApp.task.home.title' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'comment',
        data: { pageTitle: 'finspinApp.comment.home.title' },
        loadChildren: () => import('./comment/comment.module').then(m => m.CommentModule),
      },
      {
        path: 'dashboard',
        data: { pageTitle: 'finspinApp.dashboard.home.title' },
        loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
      },
      {
        path: 'board',
        data: { pageTitle: 'finspinApp.board.home.title' },
        loadChildren: () => import('./board/board.module').then(m => m.BoardModule),
      },
      {
        path: 'tile',
        data: { pageTitle: 'finspinApp.tile.home.title' },
        loadChildren: () => import('./tile/tile.module').then(m => m.TileModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'finspinApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
