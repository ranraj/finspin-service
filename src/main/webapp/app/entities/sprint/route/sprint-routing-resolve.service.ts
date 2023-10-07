import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISprint, Sprint } from '../sprint.model';
import { SprintService } from '../service/sprint.service';

@Injectable({ providedIn: 'root' })
export class SprintRoutingResolveService implements Resolve<ISprint> {
  constructor(protected service: SprintService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISprint> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sprint: HttpResponse<Sprint>) => {
          if (sprint.body) {
            return of(sprint.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Sprint());
  }
}
